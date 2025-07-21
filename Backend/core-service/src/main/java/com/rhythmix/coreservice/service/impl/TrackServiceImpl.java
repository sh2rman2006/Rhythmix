package com.rhythmix.coreservice.service.impl;

import com.rhythmix.coreservice.dto.create.AddGenreToEntityDto;
import com.rhythmix.coreservice.dto.create.TrackCreateDto;
import com.rhythmix.coreservice.dto.update.TrackUpdateDto;
import com.rhythmix.coreservice.entity.Album;
import com.rhythmix.coreservice.entity.Artist;
import com.rhythmix.coreservice.entity.Genre;
import com.rhythmix.coreservice.entity.Track;
import com.rhythmix.coreservice.exception.GenreNotFoundException;
import com.rhythmix.coreservice.exception.TrackAlreadyExistException;
import com.rhythmix.coreservice.exception.TrackNotFoundException;
import com.rhythmix.coreservice.repository.AlbumRepository;
import com.rhythmix.coreservice.repository.ArtistRepository;
import com.rhythmix.coreservice.repository.GenreRepository;
import com.rhythmix.coreservice.repository.TrackRepository;
import com.rhythmix.coreservice.service.ImageUploadService;
import com.rhythmix.coreservice.service.MinioService;
import com.rhythmix.coreservice.service.TrackService;
import com.rhythmix.coreservice.utils.MergeUtils;
import com.rhythmix.coreservice.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackServiceImpl implements TrackService {
    private final TrackRepository trackRepository;
    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final GenreRepository genreRepository;
    private final MinioService minioService;
    private final ImageUploadService imageUploadService;

    @Override
    @Transactional
    public Track createTrack(TrackCreateDto trackCreateDto, Principal principal) throws IOException {
        if (trackRepository.existsByTitleIgnoreCase(trackCreateDto.getTitle())) {
            throw new TrackAlreadyExistException("Track with title '" + trackCreateDto.getTitle() + "' already exists.");
        }

        boolean hasCoverFile = trackCreateDto.getCoverFile() != null && !trackCreateDto.getCoverFile().isEmpty();
        boolean hasCoverUrl = trackCreateDto.getCoverUrl() != null && !trackCreateDto.getCoverUrl().isBlank();

        String coverUrl = null;
        String fileUrl = null;

        if (hasCoverFile) {
            fileUrl = imageUploadService.uploadImageFile(trackCreateDto.getCoverFile(), UUID.randomUUID().toString());
        } else if (hasCoverUrl) {
            coverUrl = imageUploadService.normalizeUrl(trackCreateDto.getCoverUrl());
        }

        if (trackCreateDto.getAudioFile() == null) throw new IllegalArgumentException("No audio file provided.");

        byte[] audioBytes = trackCreateDto.getAudioFile().getBytes();
        String audioFile = minioService.uploadMusicAudio(
                new ByteArrayInputStream(audioBytes),
                UUID.randomUUID().toString(),
                trackCreateDto.getAudioFile().getContentType()
        );
        int duration = minioService.extractDuration(audioBytes);
        Instant now = Instant.now();

        Artist artist;
        if (trackCreateDto.getArtistId() == null) artist = null;
        else artist = artistRepository.findById(trackCreateDto.getArtistId()).orElse(null);

        Album album;
        if (trackCreateDto.getAlbumId() == null) album = null;
        else album = albumRepository.findById(trackCreateDto.getAlbumId()).orElse(null);

        Track track = Track.builder()
                .title(trackCreateDto.getTitle())
                .description(trackCreateDto.getDescription())
                .audioFile(audioFile)
                .coverUrl(coverUrl)
                .coverFile(fileUrl)
                .duration(duration)
                .explicit(trackCreateDto.getExplicit())
                .releaseDate(trackCreateDto.getReleaseDate())
                .uploadedAt(now)
                .uploadedBy(SecurityUtils.extractUserId(principal))
                .artist(artist)
                .album(album)
                .build();

        Track savedTrack = trackRepository.save(track);
        log.info("Created track: {}", savedTrack);

        return savedTrack;
    }

    @Override
    @Transactional
    public Track updateTrack(TrackUpdateDto trackUpdateDto) {
        Track track = trackRepository.findWithArtistAndAlbumById(trackUpdateDto.getId())
                .orElseThrow(() -> new TrackNotFoundException("Track with id: '" + trackUpdateDto.getId() + "' not found"));

        Album album = albumRepository.findWithArtistById(trackUpdateDto.getAlbumId()).orElse(null);
        if (album != null && album.getArtist().getId().equals(track.getArtist().getId())) {
            track.setAlbum(album);
        }

        boolean hasNewCoverFile = trackUpdateDto.getCoverFile() != null && !trackUpdateDto.getCoverFile().isEmpty();
        boolean hasNewCoverUrl = trackUpdateDto.getCoverUrl() != null && !trackUpdateDto.getCoverUrl().isBlank();

        if (hasNewCoverFile) {
            if (track.getCoverFile() != null) {
                minioService.delete(track.getCoverFile());
            }

            String fileUrl = imageUploadService.uploadImageFile(trackUpdateDto.getCoverFile(), UUID.randomUUID().toString());
            track.setCoverFile(fileUrl);
            track.setCoverUrl(null);
        } else if (hasNewCoverUrl) {
            if (track.getCoverFile() != null) {
                minioService.delete(track.getCoverFile());
            }

            track.setCoverUrl(trackUpdateDto.getCoverUrl());
            track.setCoverFile(null);
        }

        track.setTitle(MergeUtils.preferNewIfPresent(track.getTitle(), trackUpdateDto.getTitle()));
        track.setDescription(MergeUtils.preferNewIfPresent(track.getDescription(), trackUpdateDto.getDescription()));
        track.setExplicit(MergeUtils.preferNewIfPresent(track.getExplicit(), trackUpdateDto.getExplicit()));
        track.setReleaseDate(MergeUtils.preferNewIfPresent(track.getReleaseDate(), trackUpdateDto.getReleaseDate()));

        Track savedTrack = trackRepository.save(track);

        log.info("Updated track with id: {}", savedTrack);

        return savedTrack;
    }

    @Override
    @Transactional
    public void deleteTrack(UUID trackId) {
        Track track = trackRepository.findById(trackId).orElseThrow(
                () -> new TrackNotFoundException("Track not found with id: " + trackId)
        );
        minioService.delete(track.getCoverFile());
        trackRepository.delete(track);
        log.info("Deleted track : {}", trackId);
    }

    @Override
    @Transactional
    public Track addGenreToTrack(AddGenreToEntityDto dto) {
        Track track = trackRepository.findWithRelationsById(dto.entityId()).orElseThrow(
                () -> new TrackNotFoundException("Track not found with id '" + dto.entityId() + "'")
        );

        List<Genre> genresToAdd = genreRepository.findAllById(dto.genreIds());

        for (Genre genre : genresToAdd) {
            track.addGenre(genre);
        }

        Track updatedTrack = trackRepository.save(track);
        log.info("Updated track with genres: {}", updatedTrack);
        return updatedTrack;
    }

    @Override
    @Transactional
    public Track removeGenreFromTrack(UUID trackGenreId, UUID trackId) {
        Track track = trackRepository.findWithRelationsById(trackId).orElseThrow(
                () -> new TrackNotFoundException("Track not found with id '" + trackId + "'")
        );

        Genre genreToRemove = genreRepository.findById(trackGenreId).orElseThrow(
                () -> new GenreNotFoundException("Genre not found with id '" + trackGenreId + "'")
        );

        track.getGenres().remove(genreToRemove);
        genreToRemove.getTracks().remove(track);

        log.info("Removed genre from track: {}", track);
        return track;
    }
}
