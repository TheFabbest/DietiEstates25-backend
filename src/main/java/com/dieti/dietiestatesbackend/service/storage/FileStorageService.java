package com.dieti.dietiestatesbackend.service.storage;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface FileStorageService {
    /**
     * Carica una lista di file in un percorso specifico.
     * @param path Il percorso (es. ULID della property) dove salvare i file.
     * @param files La lista di file da caricare.
     */
    void uploadImages(String path, List<MultipartFile> files);
}