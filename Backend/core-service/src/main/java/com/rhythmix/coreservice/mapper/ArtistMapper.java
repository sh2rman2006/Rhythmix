package com.rhythmix.coreservice.mapper;

import com.rhythmix.coreservice.dto.ArtistDto;
import com.rhythmix.coreservice.entity.Artist;
import com.rhythmix.coreservice.service.MinioService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArtistMapper implements EntitiesMapper<Artist, ArtistDto> {
    private final MinioService minioService;

    @Override
    public ArtistDto toDto(@NotNull Artist artist) {
        String profileImageUrl = artist.getProfileImageUrl();
        String fileImageUrl = artist.getFileImageUrl();

        if ((profileImageUrl == null || profileImageUrl.isBlank()) && fileImageUrl != null && !fileImageUrl.isBlank()) {
            profileImageUrl = minioService.generatePresignedUrl(fileImageUrl, 60 * 60 * 24 * 7);
        }

        return new ArtistDto(
                artist.getId(),
                artist.getStageName(),
                artist.getRealName(),
                artist.getBiography(),
                artist.getCountry(),
                artist.getCity(),
                profileImageUrl,
                artist.getCreatedBy(),
                artist.getCreatedAt(),
                artist.getUpdatedAt()
        );
    }

    @Override
    public Artist toEntity(@NotNull ArtistDto artistDto) {
        return Artist.builder()
                .id(artistDto.getId())
                .stageName(artistDto.getStageName())
                .realName(artistDto.getRealName())
                .biography(artistDto.getBiography())
                .country(artistDto.getCountry())
                .city(artistDto.getCity())
                .profileImageUrl(artistDto.getProfileImageUrl())
                .createdBy(artistDto.getCreatedBy())
                .createdAt(artistDto.getCreatedAt())
                .updatedAt(artistDto.getUpdatedAt())
                .build();
    }
}
