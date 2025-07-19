package com.rhythmix.coreservice.controller;

import com.rhythmix.coreservice.dto.ArtistDto;
import com.rhythmix.coreservice.dto.create.ArtistCreateDto;
import com.rhythmix.coreservice.dto.update.ArtistUpdateDto;
import com.rhythmix.coreservice.exception.ArtistAlreadyExistException;
import com.rhythmix.coreservice.exception.ArtistNotFoundException;
import com.rhythmix.coreservice.exception.IllegalContentTypeException;
import com.rhythmix.coreservice.mapper.ArtistMapper;
import com.rhythmix.coreservice.service.ArtistService;
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

import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/artist")
@RequiredArgsConstructor
@Tag(name = "Artist", description = "API для артистов")
public class ArtistController {
    private final ArtistService artistService;
    private final ArtistMapper artistMapper;

    @Operation(summary = "Создать артиста", description = "Доступно только для модераторов")
    @PreAuthorize("hasRole('MODERATOR_RHYTHMIX')")
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArtistDto> createArtist(@Valid @ModelAttribute ArtistCreateDto artistCreateDto, Principal principal) {
        try {
            ArtistDto artistDto = artistMapper.toDto(artistService.createArtist(artistCreateDto, principal));
            return ResponseEntity.ok(artistDto);
        } catch (IOException | IllegalContentTypeException | ArtistAlreadyExistException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "изменить артиста", description = "Доступно только для модераторов")
    @PreAuthorize("hasRole('MODERATOR_RHYTHMIX')")
    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArtistDto> updateArtist(@Valid @ModelAttribute ArtistUpdateDto artistUpdateDto) {
        try {
            ArtistDto artistDto = artistMapper.toDto(artistService.updateArtist(artistUpdateDto));
            return ResponseEntity.ok(artistDto);
        } catch (IOException | IllegalContentTypeException | ArtistNotFoundException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Удалить артиста", description = "Доступно только для модераторов")
    @PreAuthorize("hasRole('MODERATOR_RHYTHMIX')")
    @DeleteMapping("/delete/{artistId}")
    public ResponseEntity<Void> deleteArtist(@PathVariable @NotNull UUID artistId) {
        try {
            artistService.deleteArtist(artistId);
            return ResponseEntity.noContent().build();
        } catch (ArtistNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
