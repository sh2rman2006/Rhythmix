package com.rhythmix.coreservice.service.impl;

import com.rhythmix.coreservice.dto.create.AddUserCustomize;
import com.rhythmix.coreservice.entity.RhythmixUser;
import com.rhythmix.coreservice.repository.RhythmixUserRepository;
import com.rhythmix.coreservice.service.ImageUploadService;
import com.rhythmix.coreservice.service.MinioService;
import com.rhythmix.coreservice.service.UserService;
import com.rhythmix.coreservice.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final RhythmixUserRepository rhythmixUserRepository;
    private final MinioService minioService;
    private final ImageUploadService imageUploadService;

    @Override
    public Optional<RhythmixUser> getUser(Principal principal) {
        return rhythmixUserRepository.findByKeycloakId(SecurityUtils.extractUserId(principal));
    }

    @Override
    public RhythmixUser addUserCustomization(AddUserCustomize addUserCustomize, Principal principal) {
        UUID userId = SecurityUtils.extractUserId(principal);

        RhythmixUser user = rhythmixUserRepository.findByKeycloakId(userId).orElseThrow(
                () -> new SecurityException("User not found"));

        boolean hasAvatarFile = addUserCustomize.avatarFile() != null && !addUserCustomize.avatarFile().isEmpty();
        boolean hasAvatarUrl = addUserCustomize.avatarUrl() != null && !addUserCustomize.avatarUrl().isBlank();

        boolean hasBackgroundFile = addUserCustomize.backgroundFile() != null && !addUserCustomize.backgroundFile().isEmpty();
        boolean hasBackgroundUrl = addUserCustomize.backgroundUrl() != null && !addUserCustomize.backgroundUrl().isBlank();

        if (hasAvatarFile) {
            deleteIfLocalFile(user.getAvatarSeed());
            String uploadedPath = imageUploadService.uploadUserImageFile(addUserCustomize.avatarFile(), UUID.randomUUID().toString());
            user.setAvatarSeed(uploadedPath);
        } else if (hasAvatarUrl) {
            deleteIfLocalFile(user.getAvatarSeed());
            user.setAvatarSeed(addUserCustomize.avatarUrl());
        }

        if (hasBackgroundFile) {
            deleteIfLocalFile(user.getBackgroundUrl());
            String uploadedPath = imageUploadService.uploadUserImageFile(addUserCustomize.backgroundFile(), UUID.randomUUID().toString());
            user.setBackgroundUrl(uploadedPath);
        } else if (hasBackgroundUrl) {
            deleteIfLocalFile(user.getBackgroundUrl());
            user.setBackgroundUrl(addUserCustomize.backgroundUrl());
        }

        RhythmixUser saved = rhythmixUserRepository.save(user);
        log.info("User {} customization updated", saved.getId());

        return saved;
    }


    private void deleteIfLocalFile(String path) {
        if (path != null && !(path.startsWith("http://") || path.startsWith("https://"))) {
            minioService.delete(path);
        }
    }
}
