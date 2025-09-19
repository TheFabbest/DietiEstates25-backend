package com.dieti.dietiestatesbackend.service.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import jakarta.annotation.PostConstruct;

@Service
public class AzureBlobStorageService implements FileStorageService {

    @Value("${storage.image.max-file-size-mb}")
    private long maxFileSizeMb;
    
    private long maxFileSizeBytes;
    
    @Value("${storage.image.max-images-per-property}")
    private int maxImagesPerProperty;
    
    @Value("${storage.image.allowed-types}")
    private String allowedImageTypesString;
    
    private List<String> allowedImageTypes;

    @PostConstruct
    public void init() {
        maxFileSizeBytes = maxFileSizeMb * 1024 * 1024;
        allowedImageTypes = Arrays.asList(allowedImageTypesString.split(","));
    }

    @Override
    public void uploadImages(String path, List<MultipartFile> files) {
        // Validazione del numero di immagini
        if (files.size() > maxImagesPerProperty) {
            throw new IllegalArgumentException("Il numero massimo di immagini per proprietà è " + maxImagesPerProperty);
        }

        for (MultipartFile file : files) {
            // Validazione del tipo di file
            String contentType = Objects.requireNonNullElse(file.getContentType(), "application/octet-stream");
            if (!allowedImageTypes.contains(contentType)) {
                throw new IllegalArgumentException("Tipo di file non consentito: " + contentType + ". Sono ammessi solo " + String.join(", ", allowedImageTypes));
            }

            // Validazione della dimensione del file
            if (file.getSize() > maxFileSizeBytes) {
                throw new IllegalArgumentException("La dimensione del file supera il limite massimo di " + maxFileSizeMb + " MB.");
            }
        }

        // TODO: Implementare la logica di upload su Azure Blob Storage
        System.out.println("Simulando l'upload di immagini su Azure per il path: " + path);
        for (MultipartFile file : files) {
            System.out.println(" - File: " + file.getOriginalFilename() + ", Size: " + file.getSize() + ", Type: " + file.getContentType());
        }
    }
}