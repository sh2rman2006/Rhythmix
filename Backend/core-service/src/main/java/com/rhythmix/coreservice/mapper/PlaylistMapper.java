package com.rhythmix.coreservice.mapper;

import com.rhythmix.coreservice.dto.PlaylistDto;
import com.rhythmix.coreservice.entity.Playlist;
import org.springframework.stereotype.Component;

@Component
public class PlaylistMapper implements EntitiesMapper<Playlist, PlaylistDto> {
    @Override
    public PlaylistDto toDto(Playlist playlist) {
        return new PlaylistDto(
                playlist.getId(),
                playlist.getName(),
                playlist.getDescription(),
                playlist.getCoverUrl(),
                playlist.getOwnerId(),
                playlist.getIsPublic(),
                playlist.getIsSystem(),
                playlist.getCreatedAt(),
                playlist.getUpdatedAt()
        );
    }

    @Override
    public Playlist toEntity(PlaylistDto playlistDto) {
        return Playlist.builder()
                .id(playlistDto.getId())
                .name(playlistDto.getName())
                .description(playlistDto.getDescription())
                .coverUrl(playlistDto.getCoverUrl())
                .ownerId(playlistDto.getOwnerId())
                .isPublic(playlistDto.getIsPublic())
                .isSystem(playlistDto.getIsSystem())
                .createdAt(playlistDto.getCreatedAt())
                .updatedAt(playlistDto.getUpdatedAt())
                .build();
    }
}
