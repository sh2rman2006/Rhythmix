package com.rhythmix.coreservice.controller;

import com.rhythmix.coreservice.dto.GenreDto;
import com.rhythmix.coreservice.dto.create.GenreCreateDto;
import com.rhythmix.coreservice.dto.update.GenreUpdateDto;
import com.rhythmix.coreservice.exception.GenreAlreadyExistException;
import com.rhythmix.coreservice.exception.GenreNotFoundException;
import com.rhythmix.coreservice.mapper.GenreMapper;
import com.rhythmix.coreservice.service.GenreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/genre")
@RequiredArgsConstructor
@Tag(name = "Genre", description = "API для жанров")
public class GenreController {
    private final GenreService genreService;
    private final GenreMapper genreMapper;

    @Operation(summary = "Создать жанр", description = "Доступно только для модераторов")
    @PreAuthorize("hasRole('MODERATOR_RHYTHMIX')")
    @PostMapping
    public ResponseEntity<GenreDto> createGenre(@Valid @RequestBody GenreCreateDto genreCreateDto) {
        try {
            GenreDto genreDto = genreMapper.toDto(genreService.createGenre(genreCreateDto));
            return ResponseEntity.ok(genreDto);
        } catch (GenreNotFoundException | GenreAlreadyExistException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Unexpected error while creating genre", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Обновить жанр", description = "Доступно только для модераторов")
    @PreAuthorize("hasRole('MODERATOR_RHYTHMIX')")
    @PutMapping
    public ResponseEntity<GenreDto> updateGenre(@Valid @RequestBody GenreUpdateDto genreUpdateDto) {
        try {
            GenreDto genreDto = genreMapper.toDto(genreService.updateGenre(genreUpdateDto));
            return ResponseEntity.ok(genreDto);
        } catch (GenreNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error while updating genre", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Удалить жанр", description = "Доступно только для модераторов")
    @PreAuthorize("hasRole('MODERATOR_RHYTHMIX')")
    @DeleteMapping("/{genreId}")
    public ResponseEntity<Void> deleteGenre(@PathVariable UUID genreId) {
        try {
            genreService.deleteGenre(genreId);
            return ResponseEntity.noContent().build();
        } catch (GenreNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error while deleting genre", e);
            return ResponseEntity.internalServerError().build();
        }
    }

}
