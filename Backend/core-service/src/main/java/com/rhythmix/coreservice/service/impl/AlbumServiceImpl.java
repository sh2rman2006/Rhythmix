package com.rhythmix.coreservice.service.impl;

import com.rhythmix.coreservice.dto.create.AlbumCreateDto;
import com.rhythmix.coreservice.dto.update.AlbumUpdateDto;
import com.rhythmix.coreservice.entity.Album;
import com.rhythmix.coreservice.entity.Artist;
import com.rhythmix.coreservice.exception.AlbumAlreadyExistException;
import com.rhythmix.coreservice.exception.AlbumNotFoundException;
import com.rhythmix.coreservice.repository.AlbumRepository;
import com.rhythmix.coreservice.repository.ArtistRepository;
import com.rhythmix.coreservice.service.AlbumService;
import com.rhythmix.coreservice.service.ImageUploadService;
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

    @Override
    @Transactional
    public Album createAlbum(AlbumCreateDto albumCreateDto) {
        if (albumRepository.existsByTitleIgnoreCase(albumCreateDto.getTitle())) {
            throw new AlbumAlreadyExistException("Album with title '" + albumCreateDto.getTitle() + "' already exists.");
        }

        String coverUrl = imageUploadService.normalizeUrl(albumCreateDto.getCoverUrl());

        String fileUrl = imageUploadService.uploadImageFile(albumCreateDto.getCoverFile(), UUID.randomUUID().toString());

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

        String fileUrl = imageUploadService.uploadImageFile(albumUpdateDto.getCoverFile(), UUID.randomUUID().toString());

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

    @Override
    @Transactional
    public void deleteAlbum(UUID albumId) {
        albumRepository.deleteById(albumId);
    }
}
