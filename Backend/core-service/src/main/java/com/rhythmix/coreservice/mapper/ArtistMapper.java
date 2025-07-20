package com.rhythmix.coreservice.mapper;

import com.rhythmix.coreservice.dto.ArtistDto;
import com.rhythmix.coreservice.entity.Artist;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArtistMapper implements EntitiesMapper<Artist, ArtistDto> {
    private final GenreMapper genreMapper;
    private final CoverUrlResolver coverUrlResolver;

    @Override
    public ArtistDto toDto(@NotNull Artist artist) {
        if (artist == null) return null;
        String profileImageUrl = coverUrlResolver.resolveCoverUrl(artist.getProfileImageUrl(), artist.getFileImageUrl());

        return new ArtistDto(
                artist.getId(),
                artist.getStageName(),
                artist.getRealName(),
                artist.getBiography(),
                artist.getCountry(),
                artist.getCity(),
                profileImageUrl,
                artist.getGenres() != null && !artist.getGenres().isEmpty()
                        ? genreMapper.toDtoList(artist.getGenres()) : null,
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

    public ArtistDto toDtoWithoutGenres(@NotNull Artist artist) {
        if (artist == null) return null;
        String profileImageUrl = coverUrlResolver.resolveCoverUrl(artist.getProfileImageUrl(), artist.getFileImageUrl());

        return new ArtistDto(
                artist.getId(),
                artist.getStageName(),
                artist.getRealName(),
                artist.getBiography(),
                artist.getCountry(),
                artist.getCity(),
                profileImageUrl,
                null,
                artist.getCreatedBy(),
                artist.getCreatedAt(),
                artist.getUpdatedAt()
        );
    }
}
