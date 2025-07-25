package com.rhythmix.coreservice.service;

import com.rhythmix.coreservice.dto.UserPlaybackEntry;

import java.util.List;
import java.util.UUID;

public interface RedisPlaybackService {
    void incrementTrackPlays(UUID trackId);

    long getTrackPlays(UUID trackId);

    void addUserPlaybackHistory(UUID userId, UUID trackId);

    List<UserPlaybackEntry> getUserPlaybackHistory(UUID userId);
}
