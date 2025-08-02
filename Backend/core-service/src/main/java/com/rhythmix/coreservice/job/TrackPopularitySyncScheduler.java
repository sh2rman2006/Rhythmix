package com.rhythmix.coreservice.job;

import com.rhythmix.coreservice.service.PlaybackSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TrackPopularitySyncScheduler {

    private final PlaybackSyncService playbackSyncService;

    @Scheduled(fixedRateString = "${playback.sync.interval-ms:60000}")
    public void syncTrackPlays() {
        log.debug("Running scheduled track play sync job...");
        playbackSyncService.syncTrackPlaysFromRedisToDatabase();
    }
}
