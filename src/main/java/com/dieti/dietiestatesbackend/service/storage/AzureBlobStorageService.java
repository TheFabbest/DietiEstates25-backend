package com.dieti.dietiestatesbackend.service.storage;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AzureBlobStorageService implements FileStorageService {

    private static final int SAS_TOKEN_EXPIRY_MINUTES = 15;
    private static final int HTTP_CREATED_STATUS = 201;

    @Value("${azure.storage.connection-string}")
    private String connectionString;

    @Value("${azure.storage.container-name}")
    private String containerName;

    @Value("${storage.image.max-images-per-property:20}")
    private int maxImagesPerProperty;

    private BlobContainerClient blobContainerClient;
    private HttpClient httpClient;
    private static final Logger logger = LoggerFactory.getLogger(AzureBlobStorageService.class);

    @PostConstruct
    public void initializeAzureStorageClient() {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
        this.blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);
        ensureContainerExists();

        // Inizializza HttpClient una sola volta per riutilizzo e pooling delle connessioni
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(java.time.Duration.ofSeconds(10))
                .build();

        logger.info("AzureBlobStorageService inizializzato per il container '{}'", containerName);
    }

    private void ensureContainerExists() {
        if (!blobContainerClient.exists()) {
            blobContainerClient.create();
        }
    }

    @Override
    public boolean uploadImages(String directoryUlid, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return true;
        }
        if (files.size() > maxImagesPerProperty) {
            logger.error("Tentativo di caricare {} immagini, ma il massimo consentito è {}.", files.size(), maxImagesPerProperty);
            return false;
        }
        return doUploadImages(directoryUlid, files);
    }

    private boolean doUploadImages(String directoryUlid, List<MultipartFile> files) {
        List<String> uploadedBlobNames = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String blobName = buildBlobName(directoryUlid, i);

            try {
                if (uploadSingleFileWithSas(this.httpClient, file, blobName)) {
                    uploadedBlobNames.add(blobName);
                } else {
                    logger.warn("Upload fallito per {}, avvio rollback per {} file già caricati.", blobName, uploadedBlobNames.size());
                    performRollback(uploadedBlobNames);
                    return false;
                }
            } catch (Exception e) {
                logger.error("Errore durante l'upload del file {}: {}", blobName, e.getMessage(), e);
                performRollback(uploadedBlobNames);
                return false;
            }
        }
        return true;
    }

    private String buildBlobName(String directoryUlid, int fileIndex) {
        return directoryUlid + "/" + fileIndex + ".webp";
    }

    private boolean uploadSingleFileWithSas(HttpClient httpClient, MultipartFile file, String blobName) {
        String sasUrl = generateWriteSasUrl(blobName);
        
        try {
            HttpRequest request = createUploadRequest(sasUrl, file);
            HttpResponse<String> response = executeUploadRequest(httpClient, request);
            
            if (!isUploadSuccessful(response)) {
                logUploadError(file, response);
                return false;
            }
            
            logSuccessfulUpload(blobName);
            return true;
            
        } catch (IOException | InterruptedException e) {
            handleUploadException(e, file);
            return false;
        }
    }

    private String generateWriteSasUrl(String blobName) {
        BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
        BlobSasPermission sasPermission = createWriteAndCreatePermission();
        OffsetDateTime expiryTime = calculateExpiryTime();
        BlobServiceSasSignatureValues sasSignatureValues = createSasSignatureValues(expiryTime, sasPermission);
        String sasToken = generateSasToken(blobClient, sasSignatureValues);
        return buildSasUrl(blobClient, sasToken);
    }

    private BlobSasPermission createWriteAndCreatePermission() {
        return new BlobSasPermission()
                .setWritePermission(true)
                .setCreatePermission(true);
    }

    private OffsetDateTime calculateExpiryTime() {
        return OffsetDateTime.now().plusMinutes(SAS_TOKEN_EXPIRY_MINUTES);
    }

    private BlobServiceSasSignatureValues createSasSignatureValues(OffsetDateTime expiryTime, BlobSasPermission sasPermission) {
        return new BlobServiceSasSignatureValues(expiryTime, sasPermission)
                .setProtocol(com.azure.storage.common.sas.SasProtocol.HTTPS_ONLY);
    }

    private String generateSasToken(BlobClient blobClient, BlobServiceSasSignatureValues sasSignatureValues) {
        return blobClient.generateSas(sasSignatureValues);
    }

    private String buildSasUrl(BlobClient blobClient, String sasToken) {
        return String.format("%s?%s", blobClient.getBlobUrl(), sasToken);
    }

    private HttpRequest createUploadRequest(String sasUrl, MultipartFile file) throws IOException {
        byte[] fileContent = file.getBytes(); // Legge l'intero contenuto del file in un array di byte
        return HttpRequest.newBuilder()
                .uri(URI.create(sasUrl))
                .header("x-ms-blob-type", "BlockBlob")
                .header("Content-Type", file.getContentType())
                .PUT(HttpRequest.BodyPublishers.ofByteArray(fileContent)) // Usa ofByteArray
                .build();
    }

    private HttpResponse<String> executeUploadRequest(HttpClient httpClient, HttpRequest request)
            throws IOException, InterruptedException {
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private boolean isUploadSuccessful(HttpResponse<String> response) {
        return response.statusCode() == HTTP_CREATED_STATUS;
    }

    private void logUploadError(MultipartFile file, HttpResponse<String> response) {
        if (logger.isErrorEnabled()) {
            String filename = sanitizeForLog(file != null ? file.getOriginalFilename() : "unknown");
            String body = sanitizeForLog(response != null ? response.body() : "null");
            int status = response != null ? response.statusCode() : -1;

            logger.error("Upload failed for file: {}. Status: {}. Response: {}", filename, status, body);
        }
    }

    private void logSuccessfulUpload(String blobName) {
        logger.info("Successfully uploaded: {} to Azure Blob Storage via SAS", blobName);
    }

    private void handleUploadException(Exception e, MultipartFile file) {
        if (e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }

        String filename = sanitizeForLog(file != null ? file.getOriginalFilename() : "unknown");
        String message = sanitizeForLog(e != null ? e.getMessage() : "null");

        logger.error("Upload error for file: {} via SAS: {}", filename, message, e);
    }

    /**
     * Esegue il rollback eliminando tutti i blob caricati con successo in questa operazione.
     * In caso di fallimento dell'eliminazione di un singolo blob, il problema viene loggato
     * ma il rollback continua per gli altri blob (best-effort).
     */
    private void performRollback(List<String> uploadedBlobNames) {
        for (String blobToDelete : uploadedBlobNames) {
            try {
                blobContainerClient.getBlobClient(blobToDelete).delete();
                logger.info("Rollback: blob {} eliminato.", blobToDelete);
            } catch (Exception e) {
                logger.error("FALLIMENTO ROLLBACK: Impossibile eliminare il blob {}. Potrebbe essere necessario un intervento manuale.", blobToDelete, e);
            }
        }
    }
    
    @Override
    public boolean deleteImages(String directoryUlid) {
        boolean allDeletionsSuccessful = true;
        
        for (BlobItem blobItem : blobContainerClient.listBlobsByHierarchy(directoryUlid + "/")) {
            if (!deleteSingleBlob(blobItem)) {
                allDeletionsSuccessful = false;
            }
        }
        return allDeletionsSuccessful;
    }

    private boolean deleteSingleBlob(BlobItem blobItem) {
        BlobClient blobClient = blobContainerClient.getBlobClient(blobItem.getName());
        
        try {
            blobClient.delete();
            return true;
        } catch (Exception e) {
            logDeletionError(blobItem.getName(), e);
            return false;
        }
    }

    private void logDeletionError(String blobName, Exception e) {
        logger.error("Failed to delete blob: {} - {}", blobName, e.getMessage(), e);
    }

    private String sanitizeForLog(String input) {
        if (input == null) return "null";
        return input.replaceAll("[\r\n\t]", "_");
    }

}