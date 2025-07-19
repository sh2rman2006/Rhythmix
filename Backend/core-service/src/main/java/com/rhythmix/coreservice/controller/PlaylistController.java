package com.rhythmix.coreservice.controller;

import com.rhythmix.coreservice.dto.PlaylistDto;
import com.rhythmix.coreservice.dto.create.PlaylistCreateDto;
import com.rhythmix.coreservice.exception.PlaylistAlreadyExistException;
import com.rhythmix.coreservice.mapper.PlaylistMapper;
import com.rhythmix.coreservice.service.PlaylistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

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
        } catch (IllegalArgumentException | PlaylistAlreadyExistException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
