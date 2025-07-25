package com.rhythmix.coreservice.service.impl;

import com.rhythmix.coreservice.enums.AllowedAudioType;
import com.rhythmix.coreservice.enums.AllowedImageType;
import com.rhythmix.coreservice.service.MinioService;
import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {
    private final MinioClient minioClient;
    private final String defaultBucket;

    @PostConstruct
    public void ensureBucketExists() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(defaultBucket).build()
            );
            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(defaultBucket).build()
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("MinIO init error: " + e.getMessage(), e);
        }
    }

    @Override
    public String uploadMusicImage(InputStream stream, String filename, String contentType) {
        if (!AllowedImageType.isValid(contentType)) throw new IllegalArgumentException("Invalid content type: " + contentType);
        return uploadToFolder("musicImages/", stream, filename, contentType);
    }

    @Override
    public String uploadUserImage(InputStream stream, String filename, String contentType) {
        if (!AllowedImageType.isValid(contentType)) throw new IllegalArgumentException("Invalid content type: " + contentType);
        return uploadToFolder("userImages/", stream, filename, contentType);
    }

    @Override
    public String uploadMusicAudio(InputStream stream, String filename, String contentType) {
        if (!AllowedAudioType.isValid(contentType)) throw new IllegalArgumentException("Invalid content type: " + contentType);
        return uploadToFolder("musicAudio/", stream, filename, contentType);
    }

    private String uploadToFolder(String folder, InputStream stream, String filename, String contentType) {
        String objectName = folder + filename;

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(defaultBucket)
                            .object(objectName)
                            .stream(stream, -1, 10485760)
                            .contentType(contentType)
                            .build()
            );
            return objectName;
        } catch (Exception e) {
            throw new RuntimeException("Upload error: " + e.getMessage(), e);
        }
    }

    @Override
    public String generatePresignedUrl(String objectName, int expirySeconds) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(defaultBucket)
                            .object(objectName)
                            .expiry(expirySeconds)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("URL generation error: " + e.getMessage(), e);
        }
    }

    @Override
    public InputStream download(String objectName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(defaultBucket)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Download error: " + e.getMessage(), e);
        }
    }

    @Override
    public int extractDuration(byte[] bytes) {
        try {
            Path tempFile = Files.createTempFile("track_", ".mp3");
            Files.write(tempFile, bytes);

            Process process = new ProcessBuilder(
                    "ffprobe",
                    "-v", "error",
                    "-i", tempFile.toAbsolutePath().toString(),
                    "-show_entries", "format=duration",
                    "-of", "default=noprint_wrappers=1:nokey=1"
            ).redirectErrorStream(true).start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                if (line == null || line.isBlank()) {
                    throw new RuntimeException("ffprobe returned no duration");
                }
                return (int) Math.round(Double.parseDouble(line));
            } finally {
                Files.deleteIfExists(tempFile);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to extract duration from temp file", e);
        }
    }

    @Override
    public void delete(String filePathOrUrl) {
        if (filePathOrUrl == null || filePathOrUrl.isBlank()) return;

        if (!filePathOrUrl.contains("/") || filePathOrUrl.contains("://")) {
            log.debug("Skip deleting non-local file: {}", filePathOrUrl);
            return;
        }

        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(defaultBucket)
                    .object(filePathOrUrl)
                    .build());

            log.info("Deleted from MinIO: {}", filePathOrUrl);
        } catch (Exception e) {
            log.warn("Failed to delete from MinIO: {}", filePathOrUrl, e);
        }
    }

}
