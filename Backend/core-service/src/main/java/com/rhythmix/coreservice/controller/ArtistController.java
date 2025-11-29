package com.rhythmix.coreservice.controller;

import com.rhythmix.coreservice.dto.ArtistDto;
import com.rhythmix.coreservice.dto.create.AddEntityLikeDto;
import com.rhythmix.coreservice.dto.create.AddGenreToEntityDto;
import com.rhythmix.coreservice.dto.create.ArtistCreateDto;
import com.rhythmix.coreservice.dto.update.ArtistUpdateDto;
import com.rhythmix.coreservice.exception.ArtistAlreadyExistException;
import com.rhythmix.coreservice.exception.ArtistNotFoundException;
import com.rhythmix.coreservice.exception.GenreNotFoundException;
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
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArtistDto> createArtist(@Valid @ModelAttribute ArtistCreateDto artistCreateDto, Principal principal) {
        try {
            ArtistDto artistDto = artistMapper.toDto(artistService.createArtist(artistCreateDto, principal));
            return ResponseEntity.ok(artistDto);
        } catch (IOException | IllegalContentTypeException | ArtistAlreadyExistException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Unexpected error while creating artist", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "изменить артиста", description = "Доступно только для модераторов")
    @PreAuthorize("hasRole('MODERATOR_RHYTHMIX')")
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArtistDto> updateArtist(@Valid @ModelAttribute ArtistUpdateDto artistUpdateDto) {
        try {
            ArtistDto artistDto = artistMapper.toDto(artistService.updateArtist(artistUpdateDto));
            return ResponseEntity.ok(artistDto);
        } catch (IOException | IllegalContentTypeException | ArtistNotFoundException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Unexpected error while updating artist", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Удалить артиста", description = "Доступно только для модераторов")
    @PreAuthorize("hasRole('MODERATOR_RHYTHMIX')")
    @DeleteMapping("/{artistId}")
    public ResponseEntity<Void> deleteArtist(@PathVariable @NotNull UUID artistId) {
        try {
            artistService.deleteArtist(artistId);
            return ResponseEntity.noContent().build();
        } catch (ArtistNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error while deleting artist", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Добавить жанры к артисту", description = "Доступно только для модераторов")
    @PreAuthorize("hasRole('MODERATOR_RHYTHMIX')")
    @PostMapping("/genres")
    public ResponseEntity<ArtistDto> addGenres(@Valid @RequestBody AddGenreToEntityDto dto) {
        try {
            ArtistDto artistDto = artistMapper.toDto(artistService.addGenres(dto));
            return ResponseEntity.ok(artistDto);
        } catch (ArtistNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error while adding genres to artist", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Удалить жанр у артиста", description = "Доступно только для модераторов")
    @PreAuthorize("hasRole('MODERATOR_RHYTHMIX')")
    @DeleteMapping("/{artistId}/genres/{genreId}")
    public ResponseEntity<ArtistDto> removeGenre(@PathVariable @NotNull UUID artistId, @PathVariable @NotNull UUID genreId) {
        try {
            ArtistDto artistDto = artistMapper.toDto(artistService.removeGenre(genreId, artistId));
            return ResponseEntity.ok(artistDto);
        } catch (ArtistNotFoundException | GenreNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error while removing genre from artist", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Лайкнуть артиста", description = "Доступно для всех пользователей")
    @PostMapping("/like")
    public ResponseEntity<Void> likeArtist(@Valid @RequestBody AddEntityLikeDto likeDto, Principal principal) {
        try {
            artistService.likeArtist(likeDto.entityId(), principal);
            return ResponseEntity.ok().build();
        } catch (ArtistNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error while liking artist", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Убрать лайк у артиста", description = "Доступно для всех пользователей")
    @DeleteMapping("/{artistId}/unlike")
    public ResponseEntity<Void> unlikeArtist(@PathVariable @NotNull UUID artistId, Principal principal) {
        try {
            artistService.unlikeArtist(artistId, principal);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error while unliking artist", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Получить информацию об артисте по ID", description = "Доступно всем пользователям")
    @GetMapping("/{artistId}")
    public ResponseEntity<ArtistDto> getArtistById(@PathVariable @NotNull UUID artistId) {
        try {
            return ResponseEntity.ok(artistMapper.toDto(artistService.getArtist(artistId)));
        } catch (ArtistNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error while getting artist by ID", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
