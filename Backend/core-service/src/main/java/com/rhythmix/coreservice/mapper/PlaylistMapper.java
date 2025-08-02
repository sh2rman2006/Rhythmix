package com.rhythmix.coreservice.mapper;

import com.rhythmix.coreservice.dto.PlaylistDto;
import com.rhythmix.coreservice.entity.Playlist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaylistMapper implements EntitiesMapper<Playlist, PlaylistDto> {
    private final CoverUrlResolver coverUrlResolver;

    @Override
    public PlaylistDto toDto(Playlist playlist) {
        return new PlaylistDto(
                playlist.getId(),
                playlist.getName(),
                playlist.getDescription(),
                coverUrlResolver.resolveCoverUrl(playlist.getCoverUrl(), playlist.getCoverFile()),
                playlist.getOwnerId(),
                playlist.getIsPublic(),
                playlist.getIsSystem(),
                playlist.getSystemType(),
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
                .systemType(playlistDto.getSystemPlaylistType())
                .createdAt(playlistDto.getCreatedAt())
                .updatedAt(playlistDto.getUpdatedAt())
                .build();
    }
}
