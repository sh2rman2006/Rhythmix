package com.rhythmix.coreservice.recommendation;

import com.google.common.hash.Hashing;
import com.rhythmix.coreservice.config.properties.VectorizerProperties;
import com.rhythmix.coreservice.entity.Genre;
import com.rhythmix.coreservice.service.RedisPlaybackService;
import com.rhythmix.coreservice.utils.VectorMathUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenreVectorizer {

    private final RedisPlaybackService redisPlaybackService;
    private final VectorizerProperties properties;

    public float[] vectorForGenre(UUID genreId, Genre genre) {
        if (genre == null) {
            log.warn("Null genre for ID: {}", genreId);
            return new float[properties.getDimension()];
        }

        try {
            float[] cached = redisPlaybackService.getGenreVector(genreId);
            if (cached != null) return cached;
        } catch (Exception e) {
            log.warn("Redis unavailable for genre vector: {}", genreId);
        }

        float[] vector = collectWeightedGenrePathVectors(genre);
        VectorMathUtils.normalize(vector);

        try {
            redisPlaybackService.putGenreVector(genreId, vector, properties.getVectorTtl());
        } catch (Exception e) {
            log.warn("Failed to put genre vector to Redis: {}", genreId);
        }

        return vector;
    }

    private float[] collectWeightedGenrePathVectors(Genre genre) {
        int dim = properties.getDimension();
        float decay = properties.getDecayFactor();

        float[] result = new float[dim];
        float weight = 1.0f;
        int maxDepth = 20;
        int depth = 0;
        Genre current = genre;

        while (current != null) {
            if (depth == maxDepth) {
                log.warn("Possible cycle detected in genre hierarchy starting from genre {}", genre.getName());
                break;
            }
            try {
                float[] vec = hashToVector(current.getName());
                for (int i = 0; i < dim; i++) {
                    result[i] += vec[i] * weight;
                }
            } catch (Exception e) {
                log.warn("Hashing failed for genre: {}", current.getName(), e);
            }
            weight *= decay;
            depth++;
            current = current.getParent();
        }

        float totalWeight = (1 - (float)Math.pow(decay, depth)) / (1 - decay);
        VectorMathUtils.divide(result, totalWeight);

        return result;
    }

    private float[] hashToVector(String input) {
        int dim = properties.getDimension();
        int seed = Hashing.murmur3_32().hashString(input, StandardCharsets.UTF_8).asInt();
        java.util.Random random = new java.util.Random(seed);
        float[] vector = new float[dim];
        for (int i = 0; i < dim; i++) {
            vector[i] = random.nextFloat() * 2 - 1;
        }
        return vector;
    }
}
