package com.rhythmix.coreservice.controller;

import com.rhythmix.coreservice.dto.ArtistDto;
import com.rhythmix.coreservice.dto.create.ArtistCreateDto;
import com.rhythmix.coreservice.exception.ArtistAlreadyExistException;
import com.rhythmix.coreservice.exception.IllegalContentTypeException;
import com.rhythmix.coreservice.mapper.ArtistMapper;
import com.rhythmix.coreservice.service.ArtistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/artist")
@RequiredArgsConstructor
@Tag(name = "Artist", description = "API для артистов")
public class ArtistController {
    private final ArtistService artistService;
    private final ArtistMapper artistMapper;

    @Operation(summary = "Создать артиста")
    @PreAuthorize("hasRole('MODERATOR_RHYTMIX')")
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArtistDto> createArtist(@Valid @ModelAttribute ArtistCreateDto artistCreateDto, Principal principal) {
        try {
            ArtistDto artistDto = artistMapper.toDto(artistService.createArtist(artistCreateDto, principal));
            return ResponseEntity.ok(artistDto);
        } catch (IOException | IllegalContentTypeException | ArtistAlreadyExistException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
