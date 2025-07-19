package com.rhythmix.coreservice.service.impl;

import com.rhythmix.coreservice.dto.create.AddTrackToPlaylistDto;
import com.rhythmix.coreservice.dto.create.PlaylistCreateDto;
import com.rhythmix.coreservice.dto.update.PlaylistUpdateDto;
import com.rhythmix.coreservice.entity.Playlist;
import com.rhythmix.coreservice.entity.PlaylistTrack;
import com.rhythmix.coreservice.entity.PlaylistTrackId;
import com.rhythmix.coreservice.entity.Track;
import com.rhythmix.coreservice.exception.*;
import com.rhythmix.coreservice.repository.PlaylistRepository;
import com.rhythmix.coreservice.repository.PlaylistTrackRepository;
import com.rhythmix.coreservice.repository.TrackRepository;
import com.rhythmix.coreservice.service.ImageUploadService;
import com.rhythmix.coreservice.service.PlaylistService;
import com.rhythmix.coreservice.utils.MergeUtils;
import com.rhythmix.coreservice.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final PlaylistTrackRepository playlistTrackRepository;
    private final TrackRepository trackRepository;
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
    public Playlist updatePlaylist(PlaylistUpdateDto playlistUpdateDto, Principal principal) {
        UUID userId = SecurityUtils.extractUserId(principal);
        Playlist playlist = playlistRepository.findById(playlistUpdateDto.getId()).orElseThrow(
                () -> new PlaylistNotFoundException("Playlist not found with id: " + playlistUpdateDto.getId()));

        if (!playlist.getOwnerId().equals(userId) || playlist.getIsSystem()) {
            throw new PlaylistAccessDeniedException("Access denied to update playlist with id: " + playlistUpdateDto.getId()
                    + " for user with id: " + userId);
        }

        String fileUrl = imageUploadService.uploadImageFile(playlistUpdateDto.getCoverFile(), UUID.randomUUID().toString());

        playlist.setName(MergeUtils.preferNewIfPresent(playlist.getName(), playlistUpdateDto.getName()));
        playlist.setDescription(MergeUtils.preferNewIfPresent(playlist.getDescription(), playlistUpdateDto.getDescription()));
        playlist.setCoverUrl(MergeUtils.preferNewIfPresent(playlist.getCoverUrl(), playlistUpdateDto.getCoverUrl()));
        playlist.setCoverFile(MergeUtils.preferNewIfPresent(playlist.getCoverFile(), fileUrl));
        playlist.setIsPublic(MergeUtils.preferNewIfPresent(playlist.getIsPublic(), playlistUpdateDto.getIsPublic()));
        playlist.setUpdatedAt(Instant.now());

        Playlist updatedPlaylist = playlistRepository.save(playlist);
        log.info("Updated playlist: {}", updatedPlaylist);
        return updatedPlaylist;
    }

    @Override
    @Transactional
    public void deletePlaylist(UUID playlistId, Principal principal) {
        UUID userId = SecurityUtils.extractUserId(principal);
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(
                () -> new PlaylistNotFoundException("Playlist not found with id: " + playlistId));

        if (!playlist.getOwnerId().equals(userId) || playlist.getIsSystem()) {
            throw new PlaylistAccessDeniedException("Access denied to update playlist with id: " + playlistId
                    + " for user with id: " + userId);
        }

        playlistRepository.deleteById(playlistId);
        log.info("User {} deleted playlist {}", userId, playlist);
    }

    @Override
    @Transactional
    public PlaylistTrack addTrackToPlaylist(AddTrackToPlaylistDto addTrackToPlaylistDto, Principal principal) {

        Playlist playlist = playlistRepository.findById(addTrackToPlaylistDto.playlistId()).orElseThrow(
                () -> new PlaylistNotFoundException("Playlist not found with id: " + addTrackToPlaylistDto.playlistId()));

        Track track = trackRepository.findWithArtistAndAlbumById(addTrackToPlaylistDto.trackId()).orElseThrow(
                () -> new TrackNotFoundException("Track not found with id: " + addTrackToPlaylistDto.trackId()));

        UUID userId = SecurityUtils.extractUserId(principal);

        if (!playlist.getOwnerId().equals(userId)) {
            throw new PlaylistAccessDeniedException("Access denied to add track to playlist with id: "
                    + addTrackToPlaylistDto.playlistId() + " for user with id: " + userId);
        }

        if (playlistTrackRepository.existsByPlaylistAndTrack(playlist, track)) {
            throw new TrackAlreadyInPlaylistException("Track with id: " + addTrackToPlaylistDto.trackId()
                    + " is already in playlist with id: " + addTrackToPlaylistDto.playlistId());
        }

        PlaylistTrackId id = new PlaylistTrackId(playlist.getId(), track.getId());
        PlaylistTrack savedTrack = playlistTrackRepository.save(
                new PlaylistTrack(
                        id,
                        playlist,
                        track,
                        Instant.now()
                )
        );

        log.info("Added track to playlist: {}", savedTrack);
        return savedTrack;
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
