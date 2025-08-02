package com.rhythmix.coreservice.service.impl;

import com.rhythmix.coreservice.dto.create.AddTrackToPlaylistDto;
import com.rhythmix.coreservice.dto.create.PlaylistCreateDto;
import com.rhythmix.coreservice.dto.update.PlaylistUpdateDto;
import com.rhythmix.coreservice.entity.*;
import com.rhythmix.coreservice.enums.LikedEntityType;
import com.rhythmix.coreservice.enums.SystemPlaylistType;
import com.rhythmix.coreservice.exception.*;
import com.rhythmix.coreservice.repository.*;
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
    private final RhythmixUserRepository userRepository;
    private final PlaylistRepository playlistRepository;
    private final PlaylistTrackRepository playlistTrackRepository;
    private final TrackRepository trackRepository;
    private final ImageUploadService imageUploadService;
    private final EntityLikeRepository entityLikeRepository;

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
            throw new PlaylistAccessDeniedException("Access denied to delete playlist with id: " + playlistId
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
    @Transactional
    public void deleteTrackFromPlaylist(UUID playlistId, UUID trackId, Principal principal) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(
                () -> new PlaylistNotFoundException("Playlist not found with id: " + playlistId));

        UUID userId = SecurityUtils.extractUserId(principal);

        if (!playlist.getOwnerId().equals(userId)) {
            throw new PlaylistAccessDeniedException(
                    "Access denied to delete track from playlist with id: " + playlistId + " for user with id: " + userId
            );
        }

        PlaylistTrackId id = new PlaylistTrackId(playlist.getId(), trackId);
        PlaylistTrack playlistTrackToDelete = playlistTrackRepository.findById(id).orElseThrow(
                () -> new PlaylistTrackNotFoundException("Track not found with id: " + id)
        );

        playlistTrackRepository.delete(playlistTrackToDelete);
        log.info("Removed track from playlist: {}", playlistTrackToDelete);
    }

    @Override
    @Transactional
    public void likeTrack(UUID trackId, Principal principal) {
        UUID userId = SecurityUtils.extractUserId(principal);
        RhythmixUser user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        Playlist liked = playlistRepository.findByOwnerIdAndSystemType(userId, SystemPlaylistType.LIKED)
                .orElseThrow(() -> new PlaylistNotFoundException("Liked playlist not found for user " + userId));

        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new TrackNotFoundException("Track not found with id: " + trackId));

        if (playlistTrackRepository.existsByPlaylistAndTrack(liked, track)) {
            throw new TrackAlreadyInPlaylistException("Track already liked.");
        }

        PlaylistTrackId id = new PlaylistTrackId(liked.getId(), track.getId());
        playlistTrackRepository.save(new PlaylistTrack(id, liked, track, Instant.now()));

        boolean likeExists = entityLikeRepository.existsByEntityTypeAndEntityIdAndUser(LikedEntityType.TRACK, trackId, user);
        if (!likeExists) {
            entityLikeRepository.save(
                    EntityLike.builder()
                            .entityType(LikedEntityType.TRACK)
                            .entityId(trackId)
                            .user(user)
                            .createdAt(Instant.now())
                            .build()
            );
        }
        log.info("Liked track: {} from playlist: {}", track, liked);
    }

    @Override
    @Transactional
    public void unlikeTrack(UUID trackId, Principal principal) {
        UUID userId = SecurityUtils.extractUserId(principal);
        RhythmixUser user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        Playlist liked = playlistRepository.findByOwnerIdAndSystemType(userId, SystemPlaylistType.LIKED)
                .orElseThrow(() -> new PlaylistNotFoundException("Liked playlist not found."));

        PlaylistTrackId id = new PlaylistTrackId(liked.getId(), trackId);
        PlaylistTrack pt = playlistTrackRepository.findById(id)
                .orElseThrow(() -> new PlaylistTrackNotFoundException("Track not liked."));
        playlistTrackRepository.delete(pt);

        EntityLike like = entityLikeRepository.findByEntityTypeAndEntityIdAndUser(LikedEntityType.TRACK, trackId, user)
                .orElseThrow(() -> new TrackNotFoundException("Track not liked"));
        entityLikeRepository.delete(like);

        log.info("Unliked track: {} from playlist: {}", trackId, liked);
    }

    @Override
    public boolean isLiked(UUID trackId, Principal principal) {
        UUID userId = SecurityUtils.extractUserId(principal);
        RhythmixUser user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        return entityLikeRepository.existsByEntityTypeAndEntityIdAndUser(LikedEntityType.TRACK, trackId, user);
    }

    @Override
    public void likePlaylist(UUID playlistId, Principal principal) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(
                () -> new PlaylistNotFoundException("Playlist not found with id: " + playlistId)
        );

        if (!playlist.getIsPublic()) {
            throw new PlaylistAccessDeniedException("Cannot like a private playlist with id: " + playlistId);
        }

        UUID userId = SecurityUtils.extractUserId(principal);
        RhythmixUser userRef = RhythmixUser.builder().id(userId).build();

        if (entityLikeRepository.existsByEntityTypeAndEntityIdAndUser(
                LikedEntityType.PLAYLIST, playlistId, userRef)) {
            throw new IllegalArgumentException("Playlist with id: " + playlistId + " is already liked by user with id: " + userId);
        }

        entityLikeRepository.save(
                EntityLike.builder()
                        .entityType(LikedEntityType.PLAYLIST)
                        .entityId(playlistId)
                        .user(userRef)
                        .createdAt(Instant.now())
                        .build()
        );

        log.info("Liked playlist: {} by user: {}", playlistId, userId);
    }

    @Override
    public void unlikePlaylist(UUID playlistId, Principal principal) {
        UUID userId = SecurityUtils.extractUserId(principal);
        RhythmixUser userRef = RhythmixUser.builder().id(userId).build();

        entityLikeRepository.findByEntityTypeAndEntityIdAndUser(
                LikedEntityType.PLAYLIST, playlistId, userRef
        ).ifPresent(entityLikeRepository::delete);

        log.info("Unliked playlist with id: {} by user: {}", playlistId, userId);
    }

    @Override
    public boolean isPlaylistLiked(UUID playlistId, Principal principal) {
        UUID userId = SecurityUtils.extractUserId(principal);
        RhythmixUser userRef = RhythmixUser.builder().id(userId).build();

        return entityLikeRepository.existsByEntityTypeAndEntityIdAndUser(
                LikedEntityType.PLAYLIST, playlistId, userRef
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
                        .systemType(SystemPlaylistType.LIKED)
                        .createdAt(now)
                        .updatedAt(now)
                        .build()
        );
    }
}
