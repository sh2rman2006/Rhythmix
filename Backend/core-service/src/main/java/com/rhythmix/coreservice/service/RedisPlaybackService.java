package com.rhythmix.coreservice.service;

import com.rhythmix.coreservice.dto.UserPlaybackEntry;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface RedisPlaybackService {
    void incrementTrackPlays(UUID trackId);

    long getTrackPlays(UUID trackId);

    void addUserPlaybackHistory(UUID userId, UUID trackId);

    List<UserPlaybackEntry> getUserPlaybackHistory(UUID userId);

    Set<UUID> getAllTrackIdsWithPlayCounts();

    void clearTrackPlays(UUID trackId);

    // Вектора жанров
    void putGenreVector(UUID genreId, float[] vector, Duration ttl);
    float[] getGenreVector(UUID genreId);

    // Вектора треков
    void putTrackVector(UUID trackId, float[] vector, Duration ttl);
    float[] getTrackVector(UUID trackId);

}
