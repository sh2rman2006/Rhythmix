package com.rhythmix.coreservice.service.impl;

import com.rhythmix.coreservice.dto.create.AlbumCreateDto;
import com.rhythmix.coreservice.dto.update.AlbumUpdateDto;
import com.rhythmix.coreservice.entity.Album;
import com.rhythmix.coreservice.entity.Artist;
import com.rhythmix.coreservice.exception.AlbumAlreadyExistException;
import com.rhythmix.coreservice.exception.AlbumNotFoundException;
import com.rhythmix.coreservice.exception.IllegalContentTypeException;
import com.rhythmix.coreservice.repository.AlbumRepository;
import com.rhythmix.coreservice.repository.ArtistRepository;
import com.rhythmix.coreservice.service.AlbumService;
import com.rhythmix.coreservice.service.MinioService;
import com.rhythmix.coreservice.utils.MergeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {
    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final MinioService minioService;

    @Override
    @Transactional
    public Album createAlbum(AlbumCreateDto albumCreateDto) {
        if (albumRepository.existsByTitleIgnoreCase(albumCreateDto.getTitle())) {
            throw new AlbumAlreadyExistException("Album with title '" + albumCreateDto.getTitle() + "' already exists.");
        }

        String coverUrl = albumCreateDto.getCoverUrl() == null
                || albumCreateDto.getCoverUrl().isBlank()
                ? null : albumCreateDto.getCoverUrl();

        String fileUrl = null;
        try {
            if (albumCreateDto.getCoverFile() != null && !albumCreateDto.getCoverFile().isEmpty()) {
                fileUrl = minioService.uploadMusicImage(
                        albumCreateDto.getCoverFile().getInputStream(),
                        UUID.randomUUID().toString(),
                        albumCreateDto.getCoverFile().getContentType()
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

        Artist artist;
        if (albumCreateDto.getArtistId() == null) artist = null;
        else artist = artistRepository.findById(albumCreateDto.getArtistId()).orElse(null);

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
        Album album = albumRepository.findWithArtistById(albumUpdateDto.getId()).orElseThrow(() -> new AlbumNotFoundException("Album with id '" + albumUpdateDto.getId() + "' not found."));

        String fileUrl = null;
        try {
            if (albumUpdateDto.getCoverFile() != null && !albumUpdateDto.getCoverFile().isEmpty()) {
                fileUrl = minioService.uploadMusicImage(
                        albumUpdateDto.getCoverFile().getInputStream(),
                        UUID.randomUUID().toString(),
                        albumUpdateDto.getCoverFile().getContentType()
                );
                album.setCoverFile(fileUrl);
            }
        } catch (IOException e) {
            log.error("Could not upload image to Minio: {}", e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.error("Not valid ContentType for upload image to Minio: {}", e.getMessage(), e);
            throw new IllegalContentTypeException("Not valid ContentType for upload image to Minio");
        } catch (Exception e) {
            log.error("Unexpected error occurred while uploading image to Minio: {}", e.getMessage(), e);
        }

        album.setTitle(MergeUtils.preferNewIfPresent(album.getTitle(), albumUpdateDto.getTitle()));
        album.setDescription(MergeUtils.preferNewIfPresent(album.getDescription(), albumUpdateDto.getDescription()));
        album.setReleaseDate(MergeUtils.preferNewIfPresent(album.getReleaseDate(), albumUpdateDto.getReleaseDate()));
        album.setUpdatedAt(Instant.now());
        album.setCoverUrl(MergeUtils.preferNewIfPresent(album.getCoverUrl(), albumUpdateDto.getCoverUrl()));
        album.setCoverFile(MergeUtils.preferNewIfPresent(album.getCoverFile(), fileUrl));

        Album albumSaved = albumRepository.save(album);
        log.info("Updated album: {}", albumSaved);
        return albumSaved;
    }
}
