package com.dieti.dietiestatesbackend.util;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * Utility class per la gestione dei path delle immagini delle proprietà.
 *
 * Questa classe fornisce metodi per costruire i path delle thumbnail e delle immagini
 * associate alle proprietà, seguendo il principio di Single Responsibility Principle.
 *
 * I valori configurabili sono esternalizzati in application.properties:
 * - property.images.base-path
 * - property.images.thumbnail-extension
 * - property.images.thumbnail-prefix
 */
@Component
public class PropertyImageUtils {
    
    private String baseResourcesPathValue;
    private String thumbnailExtensionValue;
    private String thumbnailPrefixValue;
 
    @Value("${property.images.base-path:}")
    private String baseResourcesPath;
 
    @Value("${property.images.thumbnail-extension:.webp}")
    private String thumbnailExtension;
 
    @Value("${property.images.thumbnail-prefix:01}")
    private String thumbnailPrefix;
 
    @PostConstruct
    public void init() {
        this.baseResourcesPathValue = baseResourcesPath;
        this.thumbnailExtensionValue = thumbnailExtension;
        this.thumbnailPrefixValue = thumbnailPrefix;
    }
    
    /**
     * Costruisce il path per la thumbnail di una proprietà specifica.
     *
     * @param propertyID l'ID della proprietà
     * @return il Path completo della thumbnail
     */
    public boolean isLocalStorageEnabled() {
        return baseResourcesPathValue != null && !baseResourcesPathValue.isBlank();
    }

    public Path buildThumbnailPath(long propertyID) {
        if (!isLocalStorageEnabled()) {
            throw new IllegalStateException("Local property image storage is disabled. Configure 'property.images.base-path' or use AzureBlobStorageService.");
        }
        return Paths.get(baseResourcesPathValue + propertyID + "/" + thumbnailPrefixValue + thumbnailExtensionValue);
    }
    
    /**
     * Costruisce il path per una specifica immagine di una proprietà.
     *
     * @param propertyID l'ID della proprietà
     * @param imageIndex l'indice dell'immagine (es. "01", "02", etc.)
     * @return il Path completo dell'immagine
     */
    public Path buildImagePath(long propertyID, String imageIndex) {
        if (!isLocalStorageEnabled()) {
            throw new IllegalStateException("Local property image storage is disabled. Configure 'property.images.base-path' or use AzureBlobStorageService.");
        }
        return Paths.get(baseResourcesPathValue + propertyID + "/" + imageIndex + thumbnailExtensionValue);
    }
    
    /**
     * Costruisce il path base per le immagini di una proprietà.
     *
     * @param propertyID l'ID della proprietà
     * @return il Path base delle immagini della proprietà
     */
    public Path buildPropertyImagesBasePath(long propertyID) {
        if (!isLocalStorageEnabled()) {
            throw new IllegalStateException("Local property image storage is disabled. Configure 'property.images.base-path' or use AzureBlobStorageService.");
        }
        return Paths.get(baseResourcesPathValue + propertyID);
    }
}