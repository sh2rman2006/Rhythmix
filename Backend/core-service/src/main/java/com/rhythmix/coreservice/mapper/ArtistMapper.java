package com.rhythmix.coreservice.mapper;

import com.rhythmix.coreservice.dto.ArtistDto;
import com.rhythmix.coreservice.entity.Artist;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

@Component
public class ArtistMapper implements EntitiesMapper<Artist, ArtistDto> {
    @Override
    public ArtistDto toDto(@NotNull Artist artist) {
        return new ArtistDto(
                artist.getId(),
                artist.getStageName(),
                artist.getRealName(),
                artist.getBiography(),
                artist.getCountry(),
                artist.getCity(),
                artist.getProfileImageUrl(),
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
