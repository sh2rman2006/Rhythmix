package com.rhythmix.coreservice.job;

import com.rhythmix.coreservice.entity.Track;
import com.rhythmix.coreservice.recommendation.TrackVectorizer;
import com.rhythmix.coreservice.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class VectorRefresherJob {

    private final TrackRepository trackRepository;
    private final TrackVectorizer trackVectorizer;

    @Scheduled(fixedRate = 6 * 60 * 60 * 1000)
    @Transactional
    public void refreshTrackVectors() {
        log.info("Refreshing track vectors...");

        List<Track> allTracks = trackRepository.findAllWithGenres();
        int updated = 0;

        for (Track track : allTracks) {
            try {
                trackVectorizer.vectorForTrack(track.getId(), track);
                updated++;
            } catch (Exception e) {
                log.warn("Failed to refresh vector for track: {}", track.getId(), e);
            }
        }
        log.info("Track vector refresh completed. Updated: {}", updated);
    }
}
