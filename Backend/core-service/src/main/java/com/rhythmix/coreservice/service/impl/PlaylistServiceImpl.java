package com.rhythmix.coreservice.service.impl;

import com.rhythmix.coreservice.dto.create.PlaylistCreateDto;
import com.rhythmix.coreservice.entity.Playlist;
import com.rhythmix.coreservice.exception.PlaylistAlreadyExistException;
import com.rhythmix.coreservice.repository.PlaylistRepository;
import com.rhythmix.coreservice.service.ImageUploadService;
import com.rhythmix.coreservice.service.PlaylistService;
import com.rhythmix.coreservice.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final ImageUploadService imageUploadService;

    @Override
    public Playlist createPlaylist(PlaylistCreateDto playlistCreateDto, Principal principal) {

        if (playlistRepository.existsByNameIgnoreCase(playlistCreateDto.getName())) {
            throw new PlaylistAlreadyExistException(playlistCreateDto.getName());
        }

        UUID userId = SecurityUtils.extractUserId(principal);
        String coverUrl = imageUploadService.normalizeUrl(playlistCreateDto.getCoverUrl());
        String fileUrl = imageUploadService.uploadImageFile(playlistCreateDto.getCoverFile(), UUID.randomUUID().toString());
        Instant now = Instant.now();

        return playlistRepository.save(
                Playlist.builder()
                        .name(playlistCreateDto.getName())
                        .description(playlistCreateDto.getDescription())
                        .coverUrl(coverUrl)
                        .coverFile(fileUrl)
                        .isPublic(playlistCreateDto.getIsPublic())
                        .ownerId(userId)
                        .isSystem(false)
                        .createdAt(now)
                        .updatedAt(now)
                        .build()
        );
    }

    @Override
    public Playlist createLikedPlaylistForUser(UUID userId) {
        Instant now = Instant.now();
        return playlistRepository.save(
                Playlist.builder()
                        .name("Понравившиеся")
                        .description("Системный плейлист для понравившихся треков")
                        .coverUrl(null)
                        .coverFile(null)
                        .ownerId(userId)
                        .isPublic(false)
                        .isSystem(true)
                        .createdAt(now)
                        .updatedAt(now)
                        .build()
        );
    }
}
