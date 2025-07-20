package com.rhythmix.coreservice.service.impl;

import com.rhythmix.coreservice.dto.create.AddGenreToEntityDto;
import com.rhythmix.coreservice.dto.create.ArtistCreateDto;
import com.rhythmix.coreservice.dto.update.ArtistUpdateDto;
import com.rhythmix.coreservice.entity.Artist;
import com.rhythmix.coreservice.entity.Genre;
import com.rhythmix.coreservice.exception.ArtistAlreadyExistException;
import com.rhythmix.coreservice.exception.ArtistNotFoundException;
import com.rhythmix.coreservice.repository.ArtistRepository;
import com.rhythmix.coreservice.repository.GenreRepository;
import com.rhythmix.coreservice.service.ArtistService;
import com.rhythmix.coreservice.service.ImageUploadService;
import com.rhythmix.coreservice.utils.MergeUtils;
import com.rhythmix.coreservice.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {
    private final ArtistRepository artistRepository;
    private final ImageUploadService imageUploadService;
    private final GenreRepository genreRepository;

    @Override
    @Transactional
    public Artist createArtist(ArtistCreateDto artistCreateDto, Principal principal) {
        if (artistRepository.existsByStageNameIgnoreCase(artistCreateDto.getStageName())) {
            log.error("Artist already exist with name {}", artistCreateDto.getStageName());
            throw new ArtistAlreadyExistException("Stage name already exists");
        }

        String profileUrl = imageUploadService.normalizeUrl(artistCreateDto.getProfileImageUrl());
        String fileUrl = imageUploadService.uploadImageFile(artistCreateDto.getAvatarFile(), UUID.randomUUID().toString());
        UUID ownerId = SecurityUtils.extractUserId(principal);
        Instant now = Instant.now();

        Artist artist = artistRepository.save(
                Artist.builder()
                        .stageName(artistCreateDto.getStageName())
                        .realName(artistCreateDto.getRealName())
                        .biography(artistCreateDto.getBiography())
                        .country(artistCreateDto.getCountry())
                        .city(artistCreateDto.getCity())
                        .profileImageUrl(profileUrl)
                        .fileImageUrl(fileUrl)
                        .createdBy(ownerId)
                        .createdAt(now)
                        .updatedAt(now)
                        .build()
        );

        log.info("Saved artist: {}", artist);
        return artist;
    }

    @Override
    public Artist updateArtist(ArtistUpdateDto artistUpdateDto) {
        Artist artist = artistRepository.findById(artistUpdateDto.getId()).orElseThrow(() -> new ArtistNotFoundException("Artist not with id '" + artistUpdateDto.getId() + "'not found"));

        String fileUrl = imageUploadService.uploadImageFile(artistUpdateDto.getAvatarFile(), UUID.randomUUID().toString());

        artist.setFileImageUrl(MergeUtils.preferNewIfPresent(artist.getFileImageUrl(), fileUrl));
        artist.setStageName(MergeUtils.preferNewIfPresent(artist.getStageName(), artistUpdateDto.getStageName()));
        artist.setRealName(MergeUtils.preferNewIfPresent(artist.getRealName(), artistUpdateDto.getRealName()));
        artist.setBiography(MergeUtils.preferNewIfPresent(artist.getBiography(), artistUpdateDto.getBiography()));
        artist.setCountry(MergeUtils.preferNewIfPresent(artist.getCountry(), artistUpdateDto.getCountry()));
        artist.setCity(MergeUtils.preferNewIfPresent(artist.getCity(), artistUpdateDto.getCity()));
        artist.setUpdatedAt(Instant.now());
        artist.setProfileImageUrl(MergeUtils.preferNewIfPresent(artist.getProfileImageUrl(), artistUpdateDto.getProfileImageUrl()));

        log.info("Updated artist: {}", artist);
        return artistRepository.save(artist);
    }

    @Override
    @Transactional
    public void deleteArtist(UUID artistId) {
        if (!artistRepository.existsById(artistId)) {
            throw new ArtistNotFoundException("Artist not with id '" + artistId + "' not found");
        }
        artistRepository.deleteById(artistId);
        log.info("Deleting artist {}", artistId);
    }

    @Override
    public Artist addGenres(AddGenreToEntityDto dto) {
        Artist artist = artistRepository.findWithGenresById(dto.entityId()).orElseThrow(
                () -> new ArtistNotFoundException("Artist not found with id '" + dto.entityId() + "'")
        );

        List<Genre> genresToAdd = genreRepository.findAllById(dto.genreIds());

        artist.getGenres().addAll(genresToAdd);

        for (Genre genre : genresToAdd) {
            artist.addGenre(genre);
        }

        Artist updatedArtist = artistRepository.save(artist);
        log.info("Updated artist with genres: {}", updatedArtist);
        return updatedArtist;
    }
}
