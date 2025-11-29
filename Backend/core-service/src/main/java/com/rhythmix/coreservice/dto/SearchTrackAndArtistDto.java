package com.rhythmix.coreservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SearchTrackAndArtistDto {
    private List<TrackDto> tracks;
    private List<ArtistDto> artists;
}
