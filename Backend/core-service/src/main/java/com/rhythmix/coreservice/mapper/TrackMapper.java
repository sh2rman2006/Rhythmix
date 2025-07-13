package com.rhythmix.coreservice.mapper;

import com.rhythmix.coreservice.dto.TrackDto;
import com.rhythmix.coreservice.entity.Track;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrackMapper implements EntitiesMapper<Track, TrackDto> {
    private final ArtistMapper artistMapper;
    private final AlbumMapper albumMapper;

    @Override
    public TrackDto toDto(Track track) {
        return new TrackDto(
                track.getId(),
                track.getTitle(),
                track.getDescription(),
                track.getFileUrl(),
                track.getCoverUrl(),
                track.getDuration(),
                track.getExplicit(),
                track.getReleaseDate(),
                track.getUploadedAt(),
                track.getUploadedBy(),
                artistMapper.toDto(track.getArtist()),
                albumMapper.toDto(track.getAlbum())
        );
    }

    @Override
    public Track toEntity(TrackDto trackDto) {
        return Track.builder()
                .id(trackDto.getId())
                .title(trackDto.getTitle())
                .description(trackDto.getDescription())
                .fileUrl(trackDto.getFileUrl())
                .coverUrl(trackDto.getCoverUrl())
                .duration(trackDto.getDuration())
                .explicit(trackDto.getExplicit())
                .releaseDate(trackDto.getReleaseDate())
                .uploadedAt(trackDto.getUploadedAt())
                .uploadedBy(trackDto.getUploadedBy())
                .artist(artistMapper.toEntity(trackDto.getArtist()))
                .album(albumMapper.toEntity(trackDto.getAlbum()))
                .build();
    }
}
