package com.dieti.dietiestatesbackend.service.storage;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class ImageValidationService {

    @Value("${storage.image.max-file-size-mb}")
    private int maxFileSizeMb;

    private long maxFileSizeBytes;

    @Value("${storage.image.allowed-types}")
    private String allowedImageTypesString;

    private String[] allowedImageTypes;

    // Proprietà per i magic bytes configurabili
    @Value("${storage.image.magic-bytes.image/webp}")
    private String webpMagicBytes;

    // Mappa per memorizzare i magic bytes configurabili per ogni Content-Type
    private final Map<String, byte[]> magicBytesMap = new HashMap<>();

    @jakarta.annotation.PostConstruct
    public void init() {
        maxFileSizeBytes = maxFileSizeMb * 1024L * 1024L;
        allowedImageTypes = allowedImageTypesString.split(",");
        populateMagicBytesMap();
    }

    /**
     * Popola la mappa dei magic bytes dai valori configurabili.
     */
    private void populateMagicBytesMap() {
        // Converti la stringa esadecimale in byte array per WebP
        if (webpMagicBytes != null && !webpMagicBytes.trim().isEmpty()) {
            magicBytesMap.put("image/webp", hexStringToByteArray(webpMagicBytes));
        }
    }

    /**
     * Valida un'immagine controllando tipo, dimensione e magic bytes.
     * 
     * @param inputStream InputStream dell'immagine da validare
     * @param contentType Content-Type dichiarato dell'immagine
     * @param fileSize Dimensione del file in bytes
     * @throws IllegalArgumentException se la validazione fallisce
     */
    public void validateImage(InputStream inputStream, String contentType, long fileSize) throws IOException {
        validateContentType(contentType);
        validateFileSize(fileSize);
        validateMagicBytes(inputStream, contentType);
    }

    /**
     * Valida il Content-Type dell'immagine.
     */
    private void validateContentType(String contentType) {
        String actualContentType = contentType != null ? contentType : "application/octet-stream";
        
        for (String allowedType : allowedImageTypes) {
            if (allowedType.equals(actualContentType)) {
                return;
            }
        }
        
        throw new IllegalArgumentException("Tipo di file non consentito: " + actualContentType + 
                                          ". Sono ammessi solo: " + String.join(", ", allowedImageTypes));
    }

    /**
     * Valida la dimensione del file.
     */
    private void validateFileSize(long fileSize) {
        if (fileSize > maxFileSizeBytes) {
            throw new IllegalArgumentException("La dimensione del file supera il limite massimo di " +
                                              maxFileSizeMb + " MB.");
        }
    }

    /**
     * Valida i magic bytes dell'immagine.
     */
    private void validateMagicBytes(InputStream inputStream, String contentType) throws IOException {
        // Verifica se il Content-Type è supportato per la validazione dei magic bytes
        if (!magicBytesMap.containsKey(contentType)) {
            // Se non abbiamo magic bytes configurati per questo tipo, accettiamo il file
            return;
        }

        byte[] expectedMagicBytes = magicBytesMap.get(contentType);
        
        // Utilizza BufferedInputStream per supportare mark/reset
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        bis.mark(expectedMagicBytes.length + 4);
        
        try {
            byte[] header = new byte[expectedMagicBytes.length];
            int bytesRead = IOUtils.read(bis, header);
            
            // Verifica che siano stati letti abbastanza byte
            if (bytesRead < expectedMagicBytes.length) {
                throw new IllegalArgumentException("File troppo piccolo per la validazione dei magic bytes");
            }
            
            // Confronta i byte letti con quelli attesi
            for (int i = 0; i < expectedMagicBytes.length; i++) {
                if (header[i] != expectedMagicBytes[i]) {
                    throw new IllegalArgumentException("Il file non corrisponde al formato dichiarato (" + 
                                                      contentType + "). Possibile tentativo di manipolazione.");
                }
            }
        } finally {
            try {
                bis.reset(); // Reset per permettere la rilettura completa
            } catch (IOException e) {
                throw new IOException("Impossibile resettare l'InputStream dopo la validazione", e);
            }
        }
    }

    /**
     * Converte una stringa esadecimale in un array di byte.
     */
    private byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                                 + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    public long getMaxFileSizeBytes() {
        return maxFileSizeBytes;
    }

    public String[] getAllowedImageTypes() {
        return allowedImageTypes;
    }
}