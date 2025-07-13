package com.rhythmix.coreservice.service.impl;

import com.rhythmix.coreservice.dto.create.ArtistCreateDto;
import com.rhythmix.coreservice.entity.Artist;
import com.rhythmix.coreservice.repository.ArtistRepository;
import com.rhythmix.coreservice.service.ArtistService;
import com.rhythmix.coreservice.service.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Principal;
import java.time.Instant;
import java.util.Optional;
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
    public Optional<Artist> getArtistById(UUID id) {
        return artistRepository.findById(id);
    }

    @Override
    public Optional<Artist> getArtistByStageName(String stageName) {
        return artistRepository.findByStageNameIgnoreCase(stageName);
    }

    @Override
    public Artist createArtist(ArtistCreateDto artistCreateDto, Principal principal) throws IOException {
        String profileUrl = artistCreateDto.getProfileImageUrl();

        String fileUrl = null;
        try {
            if (artistCreateDto.getAvatarFile() != null) {
                fileUrl = minioService.uploadMusicImage(
                        artistCreateDto.getAvatarFile().getInputStream(),
                        UUID.randomUUID().toString(),
                        artistCreateDto.getAvatarFile().getContentType());
            }
        } catch (IOException e) {
            log.error("Could not upload image to Minio: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error occurred while uploading image to Minio: {}", e.getMessage(), e);
        }

        UUID ownerId = extractUserId(principal);
        Instant now = Instant.now();

        return artistRepository.save(
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
    }


}
