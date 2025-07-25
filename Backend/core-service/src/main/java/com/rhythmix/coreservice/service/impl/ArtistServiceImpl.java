package com.rhythmix.coreservice.service.impl;

import com.rhythmix.coreservice.dto.create.AddGenreToEntityDto;
import com.rhythmix.coreservice.dto.create.ArtistCreateDto;
import com.rhythmix.coreservice.dto.update.ArtistUpdateDto;
import com.rhythmix.coreservice.entity.Artist;
import com.rhythmix.coreservice.entity.EntityLike;
import com.rhythmix.coreservice.entity.Genre;
import com.rhythmix.coreservice.entity.RhythmixUser;
import com.rhythmix.coreservice.enums.LikedEntityType;
import com.rhythmix.coreservice.exception.ArtistAlreadyExistException;
import com.rhythmix.coreservice.exception.ArtistNotFoundException;
import com.rhythmix.coreservice.exception.GenreNotFoundException;
import com.rhythmix.coreservice.repository.ArtistRepository;
import com.rhythmix.coreservice.repository.EntityLikeRepository;
import com.rhythmix.coreservice.repository.GenreRepository;
import com.rhythmix.coreservice.service.ArtistService;
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
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {
    private final ArtistRepository artistRepository;
    private final ImageUploadService imageUploadService;
    private final GenreRepository genreRepository;
    private final MinioService minioService;
    private final EntityLikeRepository entityLikeRepository;

    @Override
    @Transactional
    public Artist createArtist(ArtistCreateDto artistCreateDto, Principal principal) {
        if (artistRepository.existsByStageNameIgnoreCase(artistCreateDto.getStageName())) {
            log.error("Artist already exist with name {}", artistCreateDto.getStageName());
            throw new ArtistAlreadyExistException("Stage name already exists");
        }

        boolean hasCoverFile = artistCreateDto.getAvatarFile() != null && !artistCreateDto.getAvatarFile().isEmpty();
        boolean hasCoverUrl = artistCreateDto.getProfileImageUrl() != null && !artistCreateDto.getProfileImageUrl().isBlank();

        String profileUrl = null;
        String fileUrl = null;

        if (hasCoverFile) {
            fileUrl = imageUploadService.uploadImageFile(artistCreateDto.getAvatarFile(), UUID.randomUUID().toString());
        } else if (hasCoverUrl) {
            profileUrl = imageUploadService.normalizeUrl(artistCreateDto.getProfileImageUrl());
        }


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

        artist.setStageName(MergeUtils.preferNewIfPresent(artist.getStageName(), artistUpdateDto.getStageName()));
        artist.setRealName(MergeUtils.preferNewIfPresent(artist.getRealName(), artistUpdateDto.getRealName()));
        artist.setBiography(MergeUtils.preferNewIfPresent(artist.getBiography(), artistUpdateDto.getBiography()));
        artist.setCountry(MergeUtils.preferNewIfPresent(artist.getCountry(), artistUpdateDto.getCountry()));
        artist.setCity(MergeUtils.preferNewIfPresent(artist.getCity(), artistUpdateDto.getCity()));

        boolean hasNewCoverFile = artistUpdateDto.getAvatarFile() != null && !artistUpdateDto.getAvatarFile().isEmpty();
        boolean hasNewCoverUrl = artistUpdateDto.getProfileImageUrl() != null && !artistUpdateDto.getProfileImageUrl().isBlank();

        if (hasNewCoverFile) {
            if (artist.getFileImageUrl() != null) {
                minioService.delete(artist.getFileImageUrl());
            }

            String fileUrl = imageUploadService.uploadImageFile(artistUpdateDto.getAvatarFile(), UUID.randomUUID().toString());
            artist.setFileImageUrl(fileUrl);
            artist.setProfileImageUrl(null);
        } else if (hasNewCoverUrl) {
            if (artist.getFileImageUrl() != null) {
                minioService.delete(artist.getFileImageUrl());
            }

            artist.setProfileImageUrl(artistUpdateDto.getProfileImageUrl());
            artist.setFileImageUrl(null);
        }

        artist.setUpdatedAt(Instant.now());

        log.info("Updated artist: {}", artist);
        return artistRepository.save(artist);
    }

    @Override
    @Transactional
    public void deleteArtist(UUID artistId) {
        Artist artist = artistRepository.findById(artistId).orElseThrow(
                () -> new ArtistNotFoundException("Artist not with id '" + artistId + "' not found")
        );
        minioService.delete(artist.getFileImageUrl());
        artistRepository.delete(artist);
        log.info("Deleting artist {}", artist);
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

    @Override
    @Transactional
    public Artist removeGenre(UUID genreId, UUID artistId) {
        Artist artist = artistRepository.findWithGenresById(artistId).orElseThrow(
                () -> new ArtistNotFoundException("Artist not found with id '" + genreId + "'")
        );

        Genre genre = genreRepository.findById(genreId).orElseThrow(
                () -> new GenreNotFoundException("Genre not found with id '" + genreId + "'")
        );

        artist.getGenres().remove(genre);
        genre.getArtists().remove(artist);
        log.info("Removed artist with genres: {}", artist);
        return artist;
    }

    @Override
    public void likeArtist(UUID artistId, Principal principal) {
        UUID userId = SecurityUtils.extractUserId(principal);
        RhythmixUser user = RhythmixUser.builder().id(userId).build();

        if (!artistRepository.existsById(artistId)) {
            throw new ArtistNotFoundException("Artist not found with id: " + artistId);
        }

        boolean alreadyLiked = entityLikeRepository.existsByEntityTypeAndEntityIdAndUser(
                LikedEntityType.ARTIST, artistId, user);

        if (alreadyLiked) {
            throw new IllegalStateException("Artist already liked.");
        }

        EntityLike like = new EntityLike();
        like.setEntityType(LikedEntityType.ARTIST);
        like.setEntityId(artistId);
        like.setUser(user);
        like.setCreatedAt(Instant.now());

        entityLikeRepository.save(like);
        log.info("User {} liked artist {}", userId, artistId);
    }

    @Override
    @Transactional
    public void unlikeArtist(UUID artistId, Principal principal) {
        UUID userId = SecurityUtils.extractUserId(principal);
        RhythmixUser user = RhythmixUser.builder().id(userId).build();

        EntityLike like = entityLikeRepository.findByEntityTypeAndEntityIdAndUser(
                        LikedEntityType.ARTIST, artistId, user)
                .orElseThrow(() -> new IllegalStateException("Artist not liked."));

        entityLikeRepository.delete(like);
        log.info("User {} unliked artist {}", userId, artistId);
    }

    @Override
    public boolean isLiked(UUID artistId, Principal principal) {
        UUID userId = SecurityUtils.extractUserId(principal);
        RhythmixUser user = RhythmixUser.builder().id(userId).build();

        return entityLikeRepository.existsByEntityTypeAndEntityIdAndUser(
                LikedEntityType.ARTIST, artistId, user);
    }
}
