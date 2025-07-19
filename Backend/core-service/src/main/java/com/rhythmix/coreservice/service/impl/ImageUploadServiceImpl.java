package com.rhythmix.coreservice.service.impl;

import com.rhythmix.coreservice.exception.IllegalContentTypeException;
import com.rhythmix.coreservice.service.ImageUploadService;
import com.rhythmix.coreservice.service.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageUploadServiceImpl implements ImageUploadService {
    private final MinioService minioService;

    @Override
    public String normalizeUrl(String url) {
        return (url == null || url.isBlank()) ? null : url;
    }

    @Override
    public String uploadImageFile(MultipartFile file, String keyName) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            String key = (keyName == null || keyName.isBlank()) ? UUID.randomUUID().toString() : keyName;
            return minioService.uploadMusicImage(
                    file.getInputStream(),
                    key,
                    file.getContentType()
            );
        } catch (IOException e) {
            log.error("Could not upload image to Minio: {}", e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.error("Not valid ContentType for upload image to Minio: {}", e.getMessage(), e);
            throw new IllegalContentTypeException("Not valid ContentType for upload image to Minio");
        } catch (Exception e) {
            log.error("Unexpected error occurred while uploading image to Minio: {}", e.getMessage(), e);
        }
        return null;
    }

}
