package com.rhythmix.coreservice.service.impl;

import com.rhythmix.coreservice.dto.UserPlaybackEntry;
import com.rhythmix.coreservice.service.RedisPlaybackService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisPlaybackServiceImpl implements RedisPlaybackService {

    private static final int MAX_HISTORY_SIZE = 50;

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, byte[]> redisByteArrayTemplate;

    @Override
    public void incrementTrackPlays(UUID trackId) {
        redisTemplate.opsForValue().increment(trackPlaysKey(trackId), 1);
    }

    @Override
    public long getTrackPlays(UUID trackId) {
        Long plays = (Long) redisTemplate.opsForValue().get(trackPlaysKey(trackId));
        return plays != null ? plays : 0;
    }

    @Override
    public void addUserPlaybackHistory(UUID userId, UUID trackId) {
        String key = userHistoryKey(userId);
        UserPlaybackEntry entry = new UserPlaybackEntry(trackId, Instant.now());

        redisTemplate.opsForList().leftPush(key, entry);
        redisTemplate.opsForList().trim(key, 0, MAX_HISTORY_SIZE - 1);
    }

    @Override
    public List<UserPlaybackEntry> getUserPlaybackHistory(UUID userId) {
        List<Object> raw = redisTemplate.opsForList().range(userHistoryKey(userId), 0, MAX_HISTORY_SIZE - 1);

        return raw != null
                ? raw.stream()
                .filter(UserPlaybackEntry.class::isInstance)
                .map(UserPlaybackEntry.class::cast)
                .toList()
                : Collections.emptyList();
    }

    @Override
    public Set<UUID> getAllTrackIdsWithPlayCounts() {
        Set<String> keys = redisTemplate.keys(trackPlaysKeyPrefix() + "*");
        if (keys == null) return Collections.emptySet();

        return keys.stream()
                .map(k -> k.replace(trackPlaysKeyPrefix(), ""))
                .map(this::safeParseUUID)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public void clearTrackPlays(UUID trackId) {
        redisTemplate.delete(trackPlaysKey(trackId));
    }


    @Override
    public void putGenreVector(UUID genreId, float[] vector, Duration ttl) {
        if (vector == null || vector.length == 0) return;
        String key = genreVectorKey(genreId);
        byte[] bytes = floatArrayToByteArray(vector);
        redisByteArrayTemplate.opsForValue().set(key, bytes, ttl);
    }

    @Override
    public float[] getGenreVector(UUID genreId) {
        String key = genreVectorKey(genreId);
        byte[] bytes = redisByteArrayTemplate.opsForValue().get(key);
        return byteArrayToFloatArray(bytes);
    }


    @Override
    public void putTrackVector(UUID trackId, float[] vector, Duration ttl) {
        if (vector == null || vector.length == 0) return;
        String key = trackVectorKey(trackId);
        byte[] bytes = floatArrayToByteArray(vector);
        redisByteArrayTemplate.opsForValue().set(key, bytes, ttl);
    }

    @Override
    public float[] getTrackVector(UUID trackId) {
        String key = trackVectorKey(trackId);
        byte[] bytes = redisByteArrayTemplate.opsForValue().get(key);
        return byteArrayToFloatArray(bytes);
    }


    private UUID safeParseUUID(String str) {
        try {
            return UUID.fromString(str);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private byte[] floatArrayToByteArray(float[] vector) {
        ByteBuffer buffer = ByteBuffer.allocate(4 * vector.length);
        for (float v : vector) {
            buffer.putFloat(v);
        }
        return buffer.array();
    }

    private float[] byteArrayToFloatArray(byte[] bytes) {
        if (bytes == null) return null;
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        float[] vector = new float[bytes.length / 4];
        for (int i = 0; i < vector.length; i++) {
            vector[i] = buffer.getFloat();
        }
        return vector;
    }

    private static String trackPlaysKey(UUID trackId) {
        return "track:plays:" + trackId;
    }

    private static String userHistoryKey(UUID userId) {
        return "user:history:" + userId;
    }

    private static String trackPlaysKeyPrefix() {
        return "track:plays:";
    }

    public static String genreVectorKey(UUID genreId) {
        return "genre:vector:" + genreId;
    }

    public static String trackVectorKey(UUID trackId) {
        return "track:vector:" + trackId;
    }
}
