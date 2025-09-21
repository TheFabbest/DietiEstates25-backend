package com.dieti.dietiestatesbackend.service.storage;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface FileStorageService {
    boolean uploadImages(String directoryUlid, List<MultipartFile> files);
    boolean deleteImages(String directoryUlid);
}