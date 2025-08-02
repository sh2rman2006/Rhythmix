package com.rhythmix.coreservice.service;

import com.rhythmix.coreservice.entity.Track;

import java.util.List;
import java.util.UUID;

public interface RecommendationService {
    List<Track> recommendForUser(UUID userId);
}
