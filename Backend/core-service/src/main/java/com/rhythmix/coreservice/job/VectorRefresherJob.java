package com.rhythmix.coreservice.job;

import com.rhythmix.coreservice.entity.Genre;
import com.rhythmix.coreservice.entity.Track;
import com.rhythmix.coreservice.recommendation.GenreVectorizer;
import com.rhythmix.coreservice.recommendation.TrackVectorizer;
import com.rhythmix.coreservice.repository.GenreRepository;
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

    private final GenreRepository genreRepository;
    private final GenreVectorizer genreVectorizer;
    private final TrackRepository trackRepository;
    private final TrackVectorizer trackVectorizer;

    @Scheduled(fixedRate = 6 * 60 * 60 * 1000)
    @Transactional
    public void refreshTrackVectors() {
        log.info("Refreshing track and genre vectors...");

        List<Track> allTracks = trackRepository.findAllWithGenres();
        int updatedTracks = 0;
        int updatedGenres = 0;

        List<Genre> allGenres = genreRepository.findAll();

        for (Genre genre : allGenres) {
            try {
                genreVectorizer.vectorForGenre(genre.getId(), genre);
                updatedGenres++;
            } catch (Exception e) {
                log.warn("Failed to refresh vector for genre: {}", genre.getId(), e);
            }
        }

        for (Track track : allTracks) {
            try {
                trackVectorizer.vectorForTrack(track.getId(), track);
                updatedTracks++;
            } catch (Exception e) {
                log.warn("Failed to refresh vector for track: {}", track.getId(), e);
            }
        }
        log.info("Genre vector refresh completed. updatedGenres: {}", updatedGenres);
        log.info("Track vector refresh completed. updatedTracks: {}", updatedTracks);
    }
}
