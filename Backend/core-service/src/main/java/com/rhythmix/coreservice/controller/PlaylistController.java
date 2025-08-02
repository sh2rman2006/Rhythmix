package com.rhythmix.coreservice.controller;

import com.rhythmix.coreservice.dto.PlaylistDto;
import com.rhythmix.coreservice.dto.PlaylistTrackDto;
import com.rhythmix.coreservice.dto.create.AddEntityLikeDto;
import com.rhythmix.coreservice.dto.create.AddTrackToPlaylistDto;
import com.rhythmix.coreservice.dto.create.PlaylistCreateDto;
import com.rhythmix.coreservice.dto.update.PlaylistUpdateDto;
import com.rhythmix.coreservice.exception.*;
import com.rhythmix.coreservice.mapper.PlaylistMapper;
import com.rhythmix.coreservice.mapper.PlaylistTrackMapper;
import com.rhythmix.coreservice.service.PlaylistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/playlist")
@Tag(name = "Playlist", description = "API для плейлистов")
public class PlaylistController {
    private final PlaylistService playlistService;
    private final PlaylistMapper playlistMapper;
    private final PlaylistTrackMapper playlistTrackMapper;

    @Operation(summary = "Создать плейлист", description = "Доступно только для пользователей")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PlaylistDto> createPlaylist(@Valid @ModelAttribute PlaylistCreateDto playlistCreateDto,
                                                      Principal principal) {
        try {
            PlaylistDto playlistDto = playlistMapper.toDto(playlistService.createPlaylist(playlistCreateDto, principal));
            return ResponseEntity.ok(playlistDto);
        } catch (IllegalContentTypeException | PlaylistAlreadyExistException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Unexpected error while creating playlist", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Обновить плейлист", description = "Доступно только для владельца плейлиста")
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PlaylistDto> updatePlaylist(@Valid @ModelAttribute PlaylistUpdateDto playlistUpdateDto,
                                                      Principal principal) {
        try {
            PlaylistDto playlistDto = playlistMapper.toDto(playlistService.updatePlaylist(playlistUpdateDto, principal));
            return ResponseEntity.ok(playlistDto);
        } catch (IllegalContentTypeException e) {
            return ResponseEntity.badRequest().build();
        } catch (PlaylistAccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("Unexpected error while updating playlist", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Удалить плейлист", description = "Доступно только для владельца плейлиста")
    @DeleteMapping("/{playlistId}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable UUID playlistId, Principal principal) {
        try {
            playlistService.deletePlaylist(playlistId, principal);
            return ResponseEntity.noContent().build();
        } catch (PlaylistAccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (PlaylistNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error while deleting playlist", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Добавить трек в плейлист", description = "Доступно только для владельца плейлиста")
    @PostMapping("/track")
    public ResponseEntity<PlaylistTrackDto> addTrackToPlayList(@Valid @RequestBody AddTrackToPlaylistDto addTrackToPlaylistDto,
                                                               Principal principal) {
        try {
            PlaylistTrackDto playlistTrackDto = playlistTrackMapper.toDto(
                    playlistService.addTrackToPlaylist(addTrackToPlaylistDto, principal)
            );
            return ResponseEntity.ok(playlistTrackDto);
        } catch (PlaylistAccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (PlaylistNotFoundException |
                 TrackNotFoundException |
                 TrackAlreadyInPlaylistException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Unexpected error while adding track to playlist", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Удалить трек из плейлиста", description = "Доступно только для владельца плейлиста")
    @DeleteMapping("/{playlistId}/track/{trackId}")
    public ResponseEntity<Void> deleteTrackFromPlaylist(@PathVariable UUID playlistId,
                                                        @PathVariable UUID trackId,
                                                        Principal principal) {
        try {
            playlistService.deleteTrackFromPlaylist(playlistId, trackId, principal);
            return ResponseEntity.noContent().build();
        } catch (PlaylistAccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (PlaylistTrackNotFoundException | PlaylistNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error while deleting track from playlist", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Лайкнуть трек", description = "Доступно для всех пользователей")
    @PostMapping("/like")
    public ResponseEntity<Void> likeArtist(@Valid @RequestBody AddEntityLikeDto likeDto, Principal principal) {
        try {
            playlistService.likeTrack(likeDto.entityId(), principal);
            return ResponseEntity.ok().build();
        } catch (TrackNotFoundException | PlaylistNotFoundException | UserNotFoundException |
                 IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (TrackAlreadyInPlaylistException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Unexpected error while liking artist", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Убрать лайк у трека", description = "Доступно для всех пользователей")
    @PostMapping("/{trackId}/unlike")
    public ResponseEntity<Void> unlikeArtist(@PathVariable @NotNull UUID trackId, Principal principal) {
        try {
            playlistService.unlikeTrack(trackId, principal);
            return ResponseEntity.ok().build();
        } catch (TrackNotFoundException | PlaylistNotFoundException | UserNotFoundException |
                 IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error while liking artist", e);
            return ResponseEntity.internalServerError().build();
        }
    }

}
