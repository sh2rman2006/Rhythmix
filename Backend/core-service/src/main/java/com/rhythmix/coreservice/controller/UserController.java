package com.rhythmix.coreservice.controller;

import com.rhythmix.coreservice.dto.RhythmixUserDto;
import com.rhythmix.coreservice.dto.create.AddUserCustomize;
import com.rhythmix.coreservice.mapper.RhythmixUserMapper;
import com.rhythmix.coreservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "UserController", description = "API для работы с пользователями")
public class UserController {
    private final RhythmixUserMapper rhythmixUserMapper;
    private final UserService userService;

    @Operation(summary = "Получить информацию о текущем пользователе")
    @GetMapping("/me")
    public ResponseEntity<RhythmixUserDto> getUserInfo(Principal principal) {
        return userService.getUser(principal)
                .map(user -> ResponseEntity.ok(rhythmixUserMapper.toDto(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Добавить или обновить пользовательскую кастомизацию")
    @PostMapping(path = "/customize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RhythmixUserDto> addUserCustomization(@Valid @ModelAttribute AddUserCustomize addUserCustomize, Principal principal) {
        try {
            RhythmixUserDto rhythmixUserDto = rhythmixUserMapper.toDto(userService.addUserCustomization(addUserCustomize, principal));
            return ResponseEntity.ok(rhythmixUserDto);
        } catch (Exception e) {
            log.error("Unexpected error while adding customization for user", e);
            return ResponseEntity.internalServerError().build();
        }
    }

}
