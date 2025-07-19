package com.rhythmix.coreservice.controller;

import com.rhythmix.coreservice.dto.PlaylistDto;
import com.rhythmix.coreservice.dto.create.PlaylistCreateDto;
import com.rhythmix.coreservice.dto.update.PlaylistUpdateDto;
import com.rhythmix.coreservice.exception.IllegalContentTypeException;
import com.rhythmix.coreservice.exception.PlaylistAccessDeniedException;
import com.rhythmix.coreservice.exception.PlaylistAlreadyExistException;
import com.rhythmix.coreservice.exception.PlaylistNotFoundException;
import com.rhythmix.coreservice.mapper.PlaylistMapper;
import com.rhythmix.coreservice.service.PlaylistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    @Operation(summary = "Создать плейлист", description = "Доступно только для пользователей")
    @PostMapping("/create")
    public ResponseEntity<PlaylistDto> createPlaylist(@Valid @ModelAttribute PlaylistCreateDto playlistCreateDto, Principal principal) {
        try {
            PlaylistDto playlistDto = playlistMapper.toDto(playlistService.createPlaylist(playlistCreateDto, principal));
            return ResponseEntity.ok(playlistDto);
        } catch (IllegalContentTypeException | PlaylistAlreadyExistException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Обновить плейлист", description = "Доступно только для владельца плейлиста")
    @PutMapping("/update")
    public ResponseEntity<PlaylistDto> updatePlaylist(@Valid @ModelAttribute PlaylistUpdateDto playlistUpdateDto, Principal principal) {
        try {
            PlaylistDto playlistDto = playlistMapper.toDto(playlistService.updatePlaylist(playlistUpdateDto, principal));
            return ResponseEntity.ok(playlistDto);
        } catch (IllegalContentTypeException e) {
            return ResponseEntity.badRequest().build();
        } catch (PlaylistAccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Удалить плейлист", description = "Доступно только для владельца плейлиста")
    @DeleteMapping("/delete/{playlistId}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable UUID playlistId, Principal principal) {
        try {
            playlistService.deletePlaylist(playlistId, principal);
            return ResponseEntity.noContent().build();
        } catch (PlaylistAccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (PlaylistNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

}
