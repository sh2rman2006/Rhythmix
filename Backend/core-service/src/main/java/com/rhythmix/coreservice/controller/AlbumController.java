package com.rhythmix.coreservice.controller;

import com.rhythmix.coreservice.dto.AlbumDto;
import com.rhythmix.coreservice.dto.create.AddEntityLikeDto;
import com.rhythmix.coreservice.dto.create.AddTrackToAlbumDto;
import com.rhythmix.coreservice.dto.create.AlbumCreateDto;
import com.rhythmix.coreservice.dto.update.AlbumUpdateDto;
import com.rhythmix.coreservice.exception.*;
import com.rhythmix.coreservice.mapper.AlbumMapper;
import com.rhythmix.coreservice.service.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/album")
@Tag(name = "Album", description = "API для альбомов")
public class AlbumController {
    private final AlbumService albumService;
    private final AlbumMapper albumMapper;

    @Operation(summary = "Создать альбом", description = "Доступно только для модераторов")
    @PreAuthorize("hasRole('MODERATOR_RHYTHMIX')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AlbumDto> createAlbum(@Valid @ModelAttribute AlbumCreateDto albumCreateDto) {
        try {
            AlbumDto albumDto = albumMapper.toDto(albumService.createAlbum(albumCreateDto));
            return ResponseEntity.ok(albumDto);
        } catch (IllegalContentTypeException | AlbumAlreadyExistException | ArtistNotFoundException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Unexpected error while creating album", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Изменить альбом", description = "Доступно только для модераторов")
    @PreAuthorize("hasRole('MODERATOR_RHYTHMIX')")
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AlbumDto> updateAlbum(@Valid @ModelAttribute AlbumUpdateDto albumUpdateDto) {
        try {
            AlbumDto albumDto = albumMapper.toDto(albumService.updateAlbum(albumUpdateDto));
            return ResponseEntity.ok(albumDto);
        } catch (IllegalContentTypeException | AlbumNotFoundException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Unexpected error while updating album", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Удалить альбом", description = "Доступно только для модераторов")
    @PreAuthorize("hasRole('MODERATOR_RHYTHMIX')")
    @DeleteMapping("/{albumId}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable @NotNull UUID albumId) {
        try {
            albumService.deleteAlbum(albumId);
            return ResponseEntity.noContent().build();
        } catch (AlbumNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error while deleting album", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Добавить трек в альбом", description = "Доступно только для модераторов")
    @PreAuthorize("hasRole('MODERATOR_RHYTHMIX')")
    @PostMapping("/track")
    public ResponseEntity<AlbumDto> addTrackToAlbum(@Valid @RequestBody AddTrackToAlbumDto addTrackToAlbumDto) {
        try {
            AlbumDto albumDto = albumMapper.toDto(albumService.addTrackToAlbum(addTrackToAlbumDto.trackId(), addTrackToAlbumDto.albumId()));
            return ResponseEntity.ok(albumDto);
        } catch (AlbumNotFoundException | TrackNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (TrackAlreadyInAlbumException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Unexpected error while adding track to album", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Удалить трек из альбома", description = "Доступно только для модераторов")
    @PreAuthorize("hasRole('MODERATOR_RHYTHMIX')")
    @DeleteMapping("/{albumId}/track/{trackId}")
    public ResponseEntity<Void> removeTrackFromAlbum(@PathVariable @NotNull UUID albumId, @PathVariable @NotNull UUID trackId) {
        try {
            albumService.removeTrackFromAlbum(trackId, albumId);
            return ResponseEntity.noContent().build();
        } catch (AlbumNotFoundException | TrackNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (TrackNotInAlbumException | InconsistentArtistException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Unexpected error while removing track from album", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Лайкнуть альбом", description = "Доступно всем авторизованным пользователям")
    @PostMapping("/like")
    public ResponseEntity<Void> likeAlbum(@Valid @RequestBody AddEntityLikeDto addEntityLikeDto, Principal principal) {
        try {
            albumService.likeAlbum(addEntityLikeDto.entityId(), principal);
            return ResponseEntity.ok().build();
        } catch (AlbumNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error while liking album", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Убрать лайк", description = "Доступно всем авторизованным пользователям")
    @DeleteMapping("/{albumId}/unlike")
    public ResponseEntity<Void> unlikeAlbum(@PathVariable @NotNull UUID albumId, Principal principal) {
        try {
            albumService.unlikeAlbum(albumId, principal);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Unexpected error while unliking album", e);
            return ResponseEntity.internalServerError().build();
        }
    }

}
