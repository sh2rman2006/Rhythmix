package com.rhythmix.coreservice.service.impl;

import com.rhythmix.coreservice.dto.create.AlbumCreateDto;
import com.rhythmix.coreservice.dto.update.AlbumUpdateDto;
import com.rhythmix.coreservice.entity.*;
import com.rhythmix.coreservice.enums.LikedEntityType;
import com.rhythmix.coreservice.exception.*;
import com.rhythmix.coreservice.repository.AlbumRepository;
import com.rhythmix.coreservice.repository.ArtistRepository;
import com.rhythmix.coreservice.repository.EntityLikeRepository;
import com.rhythmix.coreservice.repository.TrackRepository;
import com.rhythmix.coreservice.service.AlbumService;
import com.rhythmix.coreservice.service.ImageUploadService;
import com.rhythmix.coreservice.service.MinioService;
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
public class AlbumServiceImpl implements AlbumService {
    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final ImageUploadService imageUploadService;
    private final TrackRepository trackRepository;
    private final MinioService minioService;
    private final EntityLikeRepository entityLikeRepository;

    @Override
    @Transactional
    public Album createAlbum(AlbumCreateDto albumCreateDto) {
        if (albumRepository.existsByTitleIgnoreCase(albumCreateDto.getTitle())) {
            throw new AlbumAlreadyExistException("Album with title '" + albumCreateDto.getTitle() + "' already exists.");
        }


        boolean hasCoverFile = albumCreateDto.getCoverFile() != null && !albumCreateDto.getCoverFile().isEmpty();
        boolean hasCoverUrl = albumCreateDto.getCoverUrl() != null && !albumCreateDto.getCoverUrl().isBlank();

        String coverUrl = null;
        String fileUrl = null;

        if (hasCoverFile) {
            fileUrl = imageUploadService.uploadImageFile(albumCreateDto.getCoverFile(), UUID.randomUUID().toString());
        } else if (hasCoverUrl) {
            coverUrl = imageUploadService.normalizeUrl(albumCreateDto.getCoverUrl());
        }

        Artist artist = artistRepository.findById(albumCreateDto.getArtistId()).orElseThrow(
                () -> new ArtistNotFoundException("Artist with id '" + albumCreateDto.getArtistId() + "' not found.")
        );

        Instant now = Instant.now();

        Album album = Album.builder()
                .title(albumCreateDto.getTitle())
                .description(albumCreateDto.getDescription())
                .coverUrl(coverUrl)
                .coverFile(fileUrl)
                .releaseDate(albumCreateDto.getReleaseDate())
                .artist(artist)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Album savedAlbum = albumRepository.save(album);
        log.info("Created album: {}", savedAlbum);
        return savedAlbum;
    }

    @Override
    @Transactional
    public Album updateAlbum(AlbumUpdateDto albumUpdateDto) {
        Album album = albumRepository.findWithArtistById(albumUpdateDto.getId()).orElseThrow(
                () -> new AlbumNotFoundException("Album with id '" + albumUpdateDto.getId() + "' not found."));

        album.setTitle(MergeUtils.preferNewIfPresent(album.getTitle(), albumUpdateDto.getTitle()));
        album.setDescription(MergeUtils.preferNewIfPresent(album.getDescription(), albumUpdateDto.getDescription()));
        album.setReleaseDate(MergeUtils.preferNewIfPresent(album.getReleaseDate(), albumUpdateDto.getReleaseDate()));
        album.setUpdatedAt(Instant.now());

        boolean hasNewCoverFile = albumUpdateDto.getCoverFile() != null && !albumUpdateDto.getCoverFile().isEmpty();
        boolean hasNewCoverUrl = albumUpdateDto.getCoverUrl() != null && !albumUpdateDto.getCoverUrl().isBlank();

        if (hasNewCoverFile) {
            if (album.getCoverFile() != null) {
                minioService.delete(album.getCoverFile());
            }

            String fileUrl = imageUploadService.uploadImageFile(albumUpdateDto.getCoverFile(), UUID.randomUUID().toString());
            album.setCoverFile(fileUrl);
            album.setCoverUrl(null);
        } else if (hasNewCoverUrl) {
            if (album.getCoverFile() != null) {
                minioService.delete(album.getCoverFile());
            }

            album.setCoverUrl(albumUpdateDto.getCoverUrl());
            album.setCoverFile(null);
        }

        Album albumSaved = albumRepository.save(album);
        log.info("Updated album: {}", albumSaved);
        return albumSaved;
    }

    @Override
    @Transactional
    public void deleteAlbum(UUID albumId) {
        Album album = albumRepository.findById(albumId).orElseThrow(
                () -> new AlbumNotFoundException("Album with id '" + albumId + "' not found.")
        );
        minioService.delete(album.getCoverFile());
        albumRepository.delete(album);
        log.info("Deleted album: {}", album);
    }

    @Override
    @Transactional
    public Album addTrackToAlbum(UUID trackId, UUID albumId) {
        Album targetAlbum = albumRepository.findWithArtistById(albumId).orElseThrow(
                () -> new AlbumNotFoundException("Album with id '" + albumId + "' not found.")
        );

        Track track = trackRepository.findWithArtistAndAlbumById(trackId).orElseThrow(
                () -> new TrackNotFoundException("Track with id '" + trackId + "' not found.")
        );

        if (!targetAlbum.getArtist().equals(track.getArtist())) {
            throw new InconsistentArtistException("Artist in album id: " + targetAlbum.getArtist().getId()
            + " and track artist id: " + track.getArtist().getId());
        }

        Album currentAlbum = track.getAlbum();

        if (currentAlbum != null && currentAlbum.equals(targetAlbum)) {
            throw new TrackAlreadyInAlbumException("Track with id: " + trackId + " is already in album.");
        } else if (currentAlbum != null && !currentAlbum.equals(targetAlbum)) {
            currentAlbum.getTracks().remove(track);
        }

        track.setAlbum(targetAlbum);
        targetAlbum.getTracks().add(track);
        log.info("Added track to album: {}", targetAlbum);
        return targetAlbum;
    }

    @Override
    @Transactional
    public void removeTrackFromAlbum(UUID trackId, UUID albumId) {
        Album album = albumRepository.findWithArtistById(albumId).orElseThrow(
                () -> new AlbumNotFoundException("Album with id '" + albumId + "' not found.")
        );

        Track track = trackRepository.findWithArtistAndAlbumById(trackId).orElseThrow(
                () -> new TrackNotFoundException("Track with id '" + trackId + "' not found.")
        );

        if (!track.getAlbum().equals(album)) {
            throw new TrackNotInAlbumException("Track with id '" + trackId + "' is not in album with id '" + albumId + "'.");
        }

        album.getTracks().remove(track);
        track.setAlbum(null);
        log.info("Removed track {} from album {}", trackId, albumId);
    }

    @Override
    @Transactional
    public void likeAlbum(UUID albumId, Principal principal) {
        UUID userId = SecurityUtils.extractUserId(principal);
        RhythmixUser user = RhythmixUser.builder().id(userId).build();

        if (!albumRepository.existsById(albumId)) {
            throw new AlbumNotFoundException("Album with id '" + albumId + "' not found.");
        }

        boolean alreadyLiked = entityLikeRepository.existsByEntityTypeAndEntityIdAndUser(
                LikedEntityType.ALBUM, albumId, user);

        if (alreadyLiked) {
            throw new IllegalStateException("Album already liked.");
        }

        EntityLike like = new EntityLike();
        like.setEntityType(LikedEntityType.ALBUM);
        like.setEntityId(albumId);
        like.setUser(user);
        like.setCreatedAt(Instant.now());

        entityLikeRepository.save(like);
        log.info("User {} liked album {}", userId, albumId);
    }

    @Override
    @Transactional
    public void unlikeAlbum(UUID albumId, Principal principal) {
        UUID userId = SecurityUtils.extractUserId(principal);
        RhythmixUser user = RhythmixUser.builder().id(userId).build();

        EntityLike like = entityLikeRepository.findByEntityTypeAndEntityIdAndUser(
                        LikedEntityType.ALBUM, albumId, user)
                .orElseThrow(() -> new IllegalStateException("Album not liked."));

        entityLikeRepository.delete(like);
        log.info("User {} unliked album {}", userId, albumId);
    }

    @Override
    public boolean isLiked(UUID albumId, Principal principal) {
        UUID userId = SecurityUtils.extractUserId(principal);
        RhythmixUser user = RhythmixUser.builder().id(userId).build();

        return entityLikeRepository.existsByEntityTypeAndEntityIdAndUser(
                LikedEntityType.ALBUM, albumId, user);
    }
}
