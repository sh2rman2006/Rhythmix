package com.rhythmix.coreservice.recommendation;

import com.rhythmix.coreservice.config.properties.VectorizerProperties;
import com.rhythmix.coreservice.entity.Genre;
import com.rhythmix.coreservice.entity.Track;
import com.rhythmix.coreservice.repository.TrackRepository;
import com.rhythmix.coreservice.service.RedisPlaybackService;
import com.rhythmix.coreservice.utils.VectorMathUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
    private final TrackRepository trackRepository;

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

    public float[] getVectorForTrackId(UUID trackId) {
        float[] vector = redisPlaybackService.getTrackVector(trackId);
        if (vector != null) return vector;

        return generateAndCacheVector(trackId);
    }

    private float[] generateAndCacheVector(UUID trackId) {
        Optional<Track> trackOpt = trackRepository.findById(trackId);
        if (trackOpt.isEmpty()) {
            log.warn("Track not found for vector generation: {}", trackId);
            return null;
        }

        Track track = trackOpt.get();
        float[] vector = vectorForTrack(trackId, track);

        if (isZeroVector(vector)) {
            log.debug("Generated zero vector for track: {}", trackId);
        }

        return vector;
    }

    private boolean isZeroVector(float[] vector) {
        for (float v : vector) {
            if (Math.abs(v) > 1e-6) {
                return false;
            }
        }
        return true;
    }


}