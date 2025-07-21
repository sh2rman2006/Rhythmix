package com.rhythmix.coreservice.mapper;

import com.rhythmix.coreservice.service.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CoverUrlResolver {
    private final MinioService minioService;

    public String resolveCoverUrl(String coverUrl, String fileCoverUrl) {
        if ((coverUrl == null || coverUrl.isBlank()) && fileCoverUrl != null && !fileCoverUrl.isBlank()) {
            return minioService.generatePresignedUrl(fileCoverUrl, 60 * 60 * 24 * 7);
        }
        return coverUrl;
    }

    public String resolve(String value) {
        if (value == null || value.isBlank()) return null;

        if (value.startsWith("http://") || value.startsWith("https://")) {
            return value;
        }

        return minioService.generatePresignedUrl(value, 60 * 60 * 24 * 7); // 7 дней
    }
}
