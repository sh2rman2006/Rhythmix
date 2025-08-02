package com.rhythmix.coreservice.service;

import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

public interface ImageUploadService {

    String normalizeUrl(String url);

    String uploadImageFile(MultipartFile file, @Nullable String keyName);

    String uploadUserImageFile(MultipartFile file, @Nullable String keyName);
}
