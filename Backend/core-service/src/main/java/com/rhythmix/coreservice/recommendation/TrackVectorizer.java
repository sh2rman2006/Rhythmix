package com.rhythmix.coreservice.recommendation;

import com.rhythmix.coreservice.config.properties.VectorizerProperties;
import com.rhythmix.coreservice.entity.Genre;
import com.rhythmix.coreservice.entity.Track;
import com.rhythmix.coreservice.service.RedisPlaybackService;
import com.rhythmix.coreservice.utils.VectorMathUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackVectorizer {

    private final RedisPlaybackService redisPlaybackService;
    private final GenreVectorizer genreVectorizer;
    private final VectorizerProperties properties;

    public float[] vectorForTrack(UUID trackId, Track track) {
        try {
            float[] cached = redisPlaybackService.getTrackVector(trackId);
            if (cached != null) return cached;
        } catch (Exception e) {
            log.warn("Redis unavailable for track vector: {}", trackId);
        }

        Set<Genre> genres = track.getGenres();
        if (genres == null || genres.isEmpty()) {
            float[] zeroVector = new float[properties.getDimension()];
            try {
                redisPlaybackService.putTrackVector(trackId, zeroVector, properties.getVectorTtl());
            } catch (Exception e) {
                log.warn("Failed to cache zero track vector for {}", trackId);
            }
            return zeroVector;
        }

        List<float[]> vectors = genres.parallelStream()
                .map(g -> genreVectorizer.vectorForGenre(g.getId(), g))
                .collect(Collectors.toList());

        float[] trackVector = VectorMathUtils.average(vectors, properties.getDimension());
        VectorMathUtils.normalize(trackVector);

        try {
            redisPlaybackService.putTrackVector(trackId, trackVector, properties.getVectorTtl());
        } catch (Exception e) {
            log.warn("Failed to cache track vector: {}", trackId);
        }

        return trackVector;
    }
}