package com.rhythmix.coreservice.service.impl;

import com.rhythmix.coreservice.repository.TrackRepository;
import com.rhythmix.coreservice.service.PlaybackSyncService;
import com.rhythmix.coreservice.service.RedisPlaybackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@Service
public class PlaybackSyncServiceImpl implements PlaybackSyncService {

    private final RedisPlaybackService redisPlaybackService;
    private final TrackRepository trackRepository;

    @Override
    @Transactional
    public void syncTrackPlaysFromRedisToDatabase() {
        Set<UUID> trackIds = redisPlaybackService.getAllTrackIdsWithPlayCounts();

        if (trackIds.isEmpty()) {
            log.info("No playback keys to sync.");
            return;
        }

        for (UUID trackId : trackIds) {
            long plays = redisPlaybackService.getTrackPlays(trackId);

            if (plays <= 0) continue;

            trackRepository.findById(trackId).ifPresent(track -> {
                track.setTotalListens(track.getTotalListens() + plays);
                trackRepository.save(track);

                redisPlaybackService.clearTrackPlays(trackId);

                log.info("Synchronized {} plays for track {}", plays, trackId);
            });
        }
    }
}
