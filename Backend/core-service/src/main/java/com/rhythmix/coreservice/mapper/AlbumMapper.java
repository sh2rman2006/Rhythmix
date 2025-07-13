package com.rhythmix.coreservice.mapper;

import com.rhythmix.coreservice.dto.AlbumDto;
import com.rhythmix.coreservice.entity.Album;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlbumMapper implements EntitiesMapper<Album, AlbumDto> {
    private final ArtistMapper artistMapper;

    @Override
    public AlbumDto toDto(@NotNull Album album) {
        return new AlbumDto(
                album.getId(),
                album.getTitle(),
                album.getDescription(),
                album.getCoverUrl(),
                album.getReleaseDate(),
                album.getArtist() != null ? artistMapper.toDto(album.getArtist()) : null,
                album.getCreatedAt(),
                album.getUpdatedAt()
        );
    }

    @Override
    public Album toEntity(@NotNull AlbumDto albumDto) {
        return Album.builder()
                .id(albumDto.getId())
                .title(albumDto.getTitle())
                .description(albumDto.getDescription())
                .coverUrl(albumDto.getCoverUrl())
                .releaseDate(albumDto.getReleaseDate())
                .artist(artistMapper.toEntity(albumDto.getArtist()))
                .createdAt(albumDto.getCreatedAt())
                .updatedAt(albumDto.getUpdatedAt())
                .build();
    }
}
