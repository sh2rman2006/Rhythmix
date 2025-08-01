package com.rhythmix.coreservice.controller;

import com.rhythmix.coreservice.dto.TrackDto;
import com.rhythmix.coreservice.mapper.TrackMapper;
import com.rhythmix.coreservice.service.RecommendationService;
import com.rhythmix.coreservice.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendation")
@Tag(name = "Recommendation", description = "API для рекомендаций")
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final TrackMapper trackMapper;

    @Operation(summary = "Получить рекомендации", description = "Только для авторизованных пользователей")
    @GetMapping
    public ResponseEntity<List<TrackDto>> getRecommendations(Principal principal) {
        try {
            List<TrackDto> trackDtos = trackMapper.toDtoList(recommendationService.recommendForUser(SecurityUtils.extractUserId(principal)));
            return ResponseEntity.ok(trackDtos);
        } catch (Exception e) {
            log.error("Unexpected error while getting recommendations", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
