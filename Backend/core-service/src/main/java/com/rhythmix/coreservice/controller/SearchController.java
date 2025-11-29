package com.rhythmix.coreservice.controller;

import com.rhythmix.coreservice.dto.AlbumDto;
import com.rhythmix.coreservice.dto.ArtistDto;
import com.rhythmix.coreservice.dto.SearchTrackAndArtistDto;
import com.rhythmix.coreservice.dto.TrackDto;
import com.rhythmix.coreservice.mapper.AlbumMapper;
import com.rhythmix.coreservice.mapper.ArtistMapper;
import com.rhythmix.coreservice.mapper.TrackMapper;
import com.rhythmix.coreservice.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "API для поиска")
public class SearchController {
    private final SearchService searchService;
    private final ArtistMapper artistMapper;
    private final TrackMapper trackMapper;
    private final AlbumMapper albumMapper;

    @Operation(summary = "Найти артиста по имени")
    @GetMapping("/artist")
    public ResponseEntity<List<ArtistDto>> searchArtist(@Valid @NotBlank @Size(min = 3) String name) {
        try {
            List<ArtistDto> artistDtos = searchService.searchArtist(name).stream().map(artistMapper::toDtoWithoutGenres).toList();
            if (artistDtos.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(artistDtos);
        } catch (Exception e) {
            log.error("Error searching for artist with name {}: {}", name, e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Найти трек по названию")
    @GetMapping("/track")
    public ResponseEntity<List<TrackDto>> searchTrack(@Valid @NotBlank @Size(min = 3) String name) {
        try {
            List<TrackDto> trackDtos = trackMapper.toCleanDtoList(searchService.searchTrack(name));
            if (trackDtos.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(trackDtos);
        } catch (Exception e) {
            log.error("Error searching for track with name {}: {}", name, e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Найти альбом по названию")
    @GetMapping("/album")
    public ResponseEntity<List<AlbumDto>> searchAlbum(@Valid @NotBlank @Size(min = 3) String name) {
        try {
            List<AlbumDto> albumDtos = searchService.searchAlbum(name).stream().map(albumMapper::toDtoWithoutArtist).toList();
            if (albumDtos.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(albumDtos);
        } catch (Exception e) {
            log.error("Error searching for album with name {}: {}", name, e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Общий поиск по артистам и трекам")
    @GetMapping("/all")
    public ResponseEntity<SearchTrackAndArtistDto> searchTrackAndArtist(@Valid @NotBlank @Size(min = 3) String name) {
        try {
            List<ArtistDto> artistDtos = searchService.searchArtist(name, Pageable.ofSize(5)).stream().map(artistMapper::toDtoWithoutGenres).toList();
            List<TrackDto> trackDtos = trackMapper.toCleanDtoList(searchService.searchTrack(name, Pageable.ofSize(5)));
            var searchResult = new SearchTrackAndArtistDto(trackDtos, artistDtos);
            if (searchResult.getArtists().isEmpty() && searchResult.getTracks().isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(searchResult);
        } catch (Exception e) {
            log.error("Error searching for track and artist with name {}: {}", name, e.getMessage());
            throw e;
        }
    }
}
