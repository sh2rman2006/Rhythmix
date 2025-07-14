package com.rhythmix.coreservice.controller;

import com.rhythmix.coreservice.dto.AlbumDto;
import com.rhythmix.coreservice.dto.create.AlbumCreateDto;
import com.rhythmix.coreservice.exception.AlbumAlreadyExistException;
import com.rhythmix.coreservice.exception.IllegalContentTypeException;
import com.rhythmix.coreservice.mapper.AlbumMapper;
import com.rhythmix.coreservice.service.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @PostMapping
    public ResponseEntity<AlbumDto> createAlbum(@Valid @ModelAttribute AlbumCreateDto albumCreateDto) {
        try {
            AlbumDto albumDto = albumMapper.toDto(albumService.createAlbum(albumCreateDto));
            return ResponseEntity.ok(albumDto);
        } catch (IllegalContentTypeException | AlbumAlreadyExistException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

}
