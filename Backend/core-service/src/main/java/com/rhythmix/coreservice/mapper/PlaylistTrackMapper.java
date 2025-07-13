package com.rhythmix.coreservice.mapper;

import com.rhythmix.coreservice.dto.PlaylistTrackDto;
import com.rhythmix.coreservice.entity.PlaylistTrack;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaylistTrackMapper implements EntitiesMapper<PlaylistTrack, PlaylistTrackDto> {
    private final PlaylistTrackIdMapper playlistTrackIdMapper;
    private final TrackMapper trackMapper;
    private final PlaylistMapper playlistMapper;

    @Override
    public PlaylistTrackDto toDto(PlaylistTrack playlistTrack) {
        return new PlaylistTrackDto(
                playlistTrackIdMapper.toDto(playlistTrack.getId()),
                trackMapper.toDto(playlistTrack.getTrack()),
                playlistTrack.getAddedAt()
        );
    }

    @Override
    public PlaylistTrack toEntity(PlaylistTrackDto playlistTrackDto) {
        return PlaylistTrack.builder()
                .id(playlistTrackIdMapper.toEntity(playlistTrackDto.getId()))
                .playlist(null)
                .track(trackMapper.toEntity(playlistTrackDto.getTrack()))
                .addedAt(playlistTrackDto.getAddedAt())
                .build();
    }
}
