package com.rhythmix.coreservice.service.impl;

import com.rhythmix.coreservice.dto.create.ArtistCreateDto;
import com.rhythmix.coreservice.entity.Artist;
import com.rhythmix.coreservice.exception.ArtistAlreadyExistException;
import com.rhythmix.coreservice.exception.IllegalContentTypeException;
import com.rhythmix.coreservice.repository.ArtistRepository;
import com.rhythmix.coreservice.service.ArtistService;
import com.rhythmix.coreservice.service.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.Principal;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {
    private final ArtistRepository artistRepository;
    private final MinioService minioService;

    private UUID extractUserId(Principal principal) {
        var token = (JwtAuthenticationToken) principal;
        Jwt jwt = (Jwt) token.getPrincipal();
        return UUID.fromString(jwt.getSubject());
    }

    @Override
    @Transactional
    public Artist createArtist(ArtistCreateDto artistCreateDto, Principal principal) throws IOException {
        if (artistRepository.existsByStageNameIgnoreCase(artistCreateDto.getStageName())) {
            log.error("Artist already exist with name {}", artistCreateDto.getStageName());
            throw new ArtistAlreadyExistException("Stage name already exists");
        }

        String profileUrl = artistCreateDto.getProfileImageUrl() == null
                || artistCreateDto.getProfileImageUrl().isBlank()
                ? null : artistCreateDto.getProfileImageUrl();

        String fileUrl = null;
        try {
            if (artistCreateDto.getAvatarFile() != null && !artistCreateDto.getAvatarFile().isEmpty()) {
                fileUrl = minioService.uploadMusicImage(
                        artistCreateDto.getAvatarFile().getInputStream(),
                        UUID.randomUUID().toString(),
                        artistCreateDto.getAvatarFile().getContentType());
            }
        } catch (IOException e) {
            log.error("Could not upload image to Minio: {}", e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.error("Not valid ContentType for upload image to Minio: {}", e.getMessage(), e);
            throw new IllegalContentTypeException("Not valid ContentType for upload image to Minio");
        } catch (Exception e) {
            log.error("Unexpected error occurred while uploading image to Minio: {}", e.getMessage(), e);
        }

        UUID ownerId = extractUserId(principal);
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


}
