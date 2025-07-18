package com.rhythmix.coreservice.service.impl;

import com.rhythmix.coreservice.dto.create.TrackCreateDto;
import com.rhythmix.coreservice.dto.update.TrackUpdateDto;
import com.rhythmix.coreservice.entity.Album;
import com.rhythmix.coreservice.entity.Artist;
import com.rhythmix.coreservice.entity.Track;
import com.rhythmix.coreservice.exception.IllegalContentTypeException;
import com.rhythmix.coreservice.exception.TrackAlreadyExistException;
import com.rhythmix.coreservice.exception.TrackNotFoundException;
import com.rhythmix.coreservice.repository.AlbumRepository;
import com.rhythmix.coreservice.repository.ArtistRepository;
import com.rhythmix.coreservice.repository.TrackRepository;
import com.rhythmix.coreservice.service.MinioService;
import com.rhythmix.coreservice.service.TrackService;
import com.rhythmix.coreservice.utils.MergeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackServiceImpl implements TrackService {
    private final TrackRepository trackRepository;
    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final MinioService minioService;

    private UUID extractUserId(Principal principal) {
        var token = (JwtAuthenticationToken) principal;
        Jwt jwt = (Jwt) token.getPrincipal();
        return UUID.fromString(jwt.getSubject());
    }


    @Override
    @Transactional
    public Track createTrack(TrackCreateDto trackCreateDto, Principal principal) throws IOException {
        if (trackRepository.existsByTitleIgnoreCase(trackCreateDto.getTitle())) {
            throw new TrackAlreadyExistException("Track with title '" + trackCreateDto.getTitle() + "' already exists.");
        }

        String coverUrl = trackCreateDto.getCoverUrl() == null
                || trackCreateDto.getCoverUrl().isBlank()
                ? null : trackCreateDto.getCoverUrl();

        String coverFile = null;

        try {
            if (trackCreateDto.getCoverFile() != null && !trackCreateDto.getCoverFile().isEmpty()) {
                coverFile = minioService.uploadMusicImage(
                        trackCreateDto.getCoverFile().getInputStream(),
                        trackCreateDto.getTitle(),
                        trackCreateDto.getCoverFile().getContentType()
                );

            }
        } catch (IOException e) {
            log.error("Could not upload image to Minio: {}", e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.error("Not valid ContentType for upload image to Minio: {}", e.getMessage(), e);
            throw new IllegalContentTypeException("Not valid ContentType for upload image to Minio");
        } catch (Exception e) {
            log.error("Unexpected error occurred while uploading image to Minio: {}", e.getMessage(), e);
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
                .coverFile(coverFile)
                .duration(duration)
                .explicit(trackCreateDto.getExplicit())
                .releaseDate(trackCreateDto.getReleaseDate())
                .uploadedAt(now)
                .uploadedBy(extractUserId(principal))
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
                .orElseThrow(() -> new TrackNotFoundException("Track not found with id: " + trackUpdateDto.getId() + "not found"));

        String coverFile = null;
        try {
            if (trackUpdateDto.getCoverFile() != null && !trackUpdateDto.getCoverFile().isEmpty()) {
                coverFile = minioService.uploadMusicImage(
                        trackUpdateDto.getCoverFile().getInputStream(),
                        trackUpdateDto.getTitle(),
                        trackUpdateDto.getCoverFile().getContentType()
                );

            }
        } catch (IOException e) {
            log.error("Could not upload image to Minio: {}", e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.error("Not valid ContentType for upload image to Minio: {}", e.getMessage(), e);
            throw new IllegalContentTypeException("Not valid ContentType for upload image to Minio");
        } catch (Exception e) {
            log.error("Unexpected error occurred while uploading image to Minio: {}", e.getMessage(), e);
        }

        Album album = albumRepository.findWithArtistById(trackUpdateDto.getAlbumId()).orElse(null);
        if (album != null && album.getArtist().getId().equals(track.getArtist().getId())) {
            track.setAlbum(album);
        }

        track.setTitle(MergeUtils.preferNewIfPresent(track.getTitle(), trackUpdateDto.getTitle()));
        track.setDescription(MergeUtils.preferNewIfPresent(track.getDescription(), trackUpdateDto.getDescription()));
        track.setCoverUrl(MergeUtils.preferNewIfPresent(track.getCoverUrl(), trackUpdateDto.getCoverUrl()));
        track.setCoverFile(MergeUtils.preferNewIfPresent(track.getCoverFile(), coverFile));
        track.setExplicit(MergeUtils.preferNewIfPresent(track.getExplicit(), trackUpdateDto.getExplicit()));
        track.setReleaseDate(MergeUtils.preferNewIfPresent(track.getReleaseDate(), trackUpdateDto.getReleaseDate()));

        Track savedTrack = trackRepository.save(track);

        log.info("Updated track: {}", savedTrack);

        return savedTrack;
    }
}
