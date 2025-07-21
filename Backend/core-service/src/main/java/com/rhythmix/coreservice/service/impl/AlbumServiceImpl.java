package com.rhythmix.coreservice.service.impl;

import com.rhythmix.coreservice.dto.create.AlbumCreateDto;
import com.rhythmix.coreservice.dto.update.AlbumUpdateDto;
import com.rhythmix.coreservice.entity.Album;
import com.rhythmix.coreservice.entity.Artist;
import com.rhythmix.coreservice.entity.Track;
import com.rhythmix.coreservice.exception.*;
import com.rhythmix.coreservice.repository.AlbumRepository;
import com.rhythmix.coreservice.repository.ArtistRepository;
import com.rhythmix.coreservice.repository.TrackRepository;
import com.rhythmix.coreservice.service.AlbumService;
import com.rhythmix.coreservice.service.ImageUploadService;
import com.rhythmix.coreservice.service.MinioService;
import com.rhythmix.coreservice.utils.MergeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
