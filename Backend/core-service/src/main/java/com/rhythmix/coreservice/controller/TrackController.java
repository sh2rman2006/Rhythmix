package com.rhythmix.coreservice.controller;

import com.rhythmix.coreservice.dto.TrackDto;
import com.rhythmix.coreservice.dto.create.TrackCreateDto;
import com.rhythmix.coreservice.dto.update.TrackUpdateDto;
import com.rhythmix.coreservice.exception.IllegalContentTypeException;
import com.rhythmix.coreservice.exception.TrackAlreadyExistException;
import com.rhythmix.coreservice.exception.TrackNotFoundException;
import com.rhythmix.coreservice.mapper.TrackMapper;
import com.rhythmix.coreservice.service.TrackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/track")
@Tag(name = "Track", description = "API для треков")
public class TrackController {
    private final TrackService trackService;
    private final TrackMapper trackMapper;

    @Operation(summary = "Создать трек", description = "Доступно только для модераторов")
    @PreAuthorize("hasRole('MODERATOR_RHYTHMIX')")
    @PostMapping("/create")
    public ResponseEntity<TrackDto> createAlbum(@Valid @ModelAttribute TrackCreateDto trackCreateDto, Principal principal) {
        try {
            TrackDto trackDto = trackMapper.toDto(trackService.createTrack(trackCreateDto, principal));
            return ResponseEntity.ok(trackDto);
        } catch (IllegalContentTypeException | TrackAlreadyExistException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Обновить трек", description = "Доступно только для модераторов")
    @PreAuthorize("hasRole('MODERATOR_RHYTHMIX')")
    @PutMapping("/update")
    public ResponseEntity<TrackDto> updateTrack(@Valid @ModelAttribute TrackUpdateDto trackUpdateDto) {
        try {
            TrackDto trackDto = trackMapper.toDto(trackService.updateTrack(trackUpdateDto));
            return ResponseEntity.ok(trackDto);
        } catch (IllegalContentTypeException | TrackNotFoundException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Удалить трек", description = "Доступно только для модераторов")
    @PreAuthorize("hasRole('MODERATOR_RHYTHMIX')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTrack(@PathVariable @NotNull UUID id) {
        try {
            trackService.deleteTrack(id);
            return ResponseEntity.noContent().build();
        } catch (TrackNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

}
