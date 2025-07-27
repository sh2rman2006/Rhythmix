package com.rhythmix.coreservice.service.impl;

import com.rhythmix.coreservice.dto.UserPlaybackEntry;
import com.rhythmix.coreservice.entity.Track;
import com.rhythmix.coreservice.recommendation.TrackVectorizer;
import com.rhythmix.coreservice.repository.TrackRepository;
import com.rhythmix.coreservice.service.RecommendationService;
import com.rhythmix.coreservice.service.RedisPlaybackService;
import com.rhythmix.coreservice.utils.VectorMathUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final RedisPlaybackService redisPlaybackService;
    private final TrackVectorizer trackVectorizer;
    private final TrackRepository trackRepository;

    private static final int MAX_RECOMMENDATIONS = 20;
    private static final int MAX_HISTORY_FOR_AVG = 50;
    private static final int HISTORY_SIZE = 50;

    @Override
    public List<Track> recommendForUser(UUID userId) {
        List<UserPlaybackEntry> history = redisPlaybackService.getUserPlaybackHistory(userId);
        if (history.isEmpty()) {
            return getFallbackRecommendations();
        }

        Set<UUID> recentTrackIds = getRecentUniqueTrackIds(history);

        float[] userVector = createUserVector(history, recentTrackIds);
        if (userVector == null) return getFallbackRecommendations();

        List<Track> candidates = getRecommendationCandidates(recentTrackIds);

        return rankTracksBySimilarity(userVector, candidates);
    }

    private Set<UUID> getRecentUniqueTrackIds(List<UserPlaybackEntry> history) {
        LinkedHashSet<UUID> uniqueIds = new LinkedHashSet<>();
        for (UserPlaybackEntry entry : history) {
            uniqueIds.remove(entry.getTrackId());
            uniqueIds.add(entry.getTrackId());

            if (uniqueIds.size() >= HISTORY_SIZE) break;
        }
        return uniqueIds;
    }

    private float[] createUserVector(List<UserPlaybackEntry> history, Set<UUID> recentTrackIds) {
        List<float[]> vectors = history.stream()
                .limit(MAX_HISTORY_FOR_AVG)
                .map(UserPlaybackEntry::getTrackId)
                .distinct()
                .map(trackVectorizer::getVectorForTrackId)
                .filter(Objects::nonNull)
                .toList();

        if (vectors.isEmpty()) return null;

        float[] userVector = VectorMathUtils.average(
                vectors,
                vectors.getFirst().length
        );
        VectorMathUtils.normalize(userVector);
        return userVector;
    }

    private List<Track> getRecommendationCandidates(Set<UUID> excludedTrackIds) {
        return trackRepository.findAll().stream()
                .filter(track -> !excludedTrackIds.contains(track.getId()))
                .collect(Collectors.toList());
    }

    private List<Track> rankTracksBySimilarity(float[] userVector, List<Track> candidates) {
        Map<UUID, float[]> vectorMap = candidates.parallelStream()
                .collect(Collectors.toMap(
                        Track::getId,
                        track -> trackVectorizer.getVectorForTrackId(track.getId())
                ));

        return candidates.stream()
                .map(track -> new TrackScore(
                        track,
                        VectorMathUtils.cosineSimilarity(userVector, vectorMap.get(track.getId()))
                ))
                .sorted(Comparator.comparingDouble(TrackScore::score).reversed())
                .limit(MAX_RECOMMENDATIONS)
                .map(TrackScore::track)
                .collect(Collectors.toList());
    }

    private List<Track> getFallbackRecommendations() {
        return trackRepository.findTop20ByOrderByTotalListensDesc();
    }

    private record TrackScore(Track track, float score) {
    }
}