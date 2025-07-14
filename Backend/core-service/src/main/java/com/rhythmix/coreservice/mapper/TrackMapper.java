package com.rhythmix.coreservice.mapper;

import com.rhythmix.coreservice.dto.TrackDto;
import com.rhythmix.coreservice.entity.Track;
import com.rhythmix.coreservice.service.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrackMapper implements EntitiesMapper<Track, TrackDto> {
    private final MinioService minioService;
    private final ArtistMapper artistMapper;
    private final AlbumMapper albumMapper;

    @Override
    public TrackDto toDto(Track track) {
        if (track == null) return null;
        String coverUrl = track.getCoverUrl();
        String fileCoverUrl = track.getCoverFile();
        if ((coverUrl == null || coverUrl.isBlank()) && fileCoverUrl != null && !fileCoverUrl.isBlank()) {
            coverUrl = minioService.generatePresignedUrl(fileCoverUrl, 60 * 60 * 24 * 7);
        }

        String audioUrl = minioService.generatePresignedUrl(track.getAudioFile(), 60 * 60 * 24 * 7);

        return new TrackDto(
                track.getId(),
                track.getTitle(),
                track.getDescription(),
                audioUrl,
                coverUrl,
                track.getDuration(),
                track.getExplicit(),
                track.getReleaseDate(),
                track.getUploadedAt(),
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
                .coverUrl(trackDto.getCoverUrl())
                .duration(trackDto.getDuration())
                .explicit(trackDto.getExplicit())
                .releaseDate(trackDto.getReleaseDate())
                .uploadedAt(trackDto.getUploadedAt())
                .artist(artistMapper.toEntity(trackDto.getArtist()))
                .album(albumMapper.toEntity(trackDto.getAlbum()))
                .build();
    }
}
