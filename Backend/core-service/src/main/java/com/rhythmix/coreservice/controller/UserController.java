package com.rhythmix.coreservice.controller;

import com.rhythmix.coreservice.dto.RhythmixUserDto;
import com.rhythmix.coreservice.mapper.RhythmixUserMapper;
import com.rhythmix.coreservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final RhythmixUserMapper rhythmixUserMapper;
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<RhythmixUserDto> getUserInfo(Principal principal) {
        return userService.getUser(principal)
                .map(user -> ResponseEntity.ok(rhythmixUserMapper.toDto(user)))
                .orElse(ResponseEntity.notFound().build());
    }

}
