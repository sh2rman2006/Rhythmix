package com.rhythmix.coreservice.mapper;

import com.rhythmix.coreservice.dto.PlaylistTrackIdDto;
import com.rhythmix.coreservice.entity.PlaylistTrackId;
import org.springframework.stereotype.Component;

@Component
public class PlaylistTrackIdMapper implements EntitiesMapper<PlaylistTrackId, PlaylistTrackIdDto> {

    @Override
    public PlaylistTrackIdDto toDto(PlaylistTrackId playlistTrackId) {
        return new PlaylistTrackIdDto(
                playlistTrackId.getPlaylistId(),
                playlistTrackId.getTrackId()
        );
    }

    @Override
    public PlaylistTrackId toEntity(PlaylistTrackIdDto playlistTrackIdDto) {
        return new PlaylistTrackId(
                playlistTrackIdDto.getPlaylistId(),
                playlistTrackIdDto.getTrackId()
        );
    }
}
