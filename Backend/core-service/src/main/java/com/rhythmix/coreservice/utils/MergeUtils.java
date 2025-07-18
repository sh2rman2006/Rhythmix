package com.rhythmix.coreservice.utils;

public final class MergeUtils {
    public static <T> T preferNewIfPresent(T oldValue, T newValue) {
        if (newValue == null) return oldValue;
        if (newValue instanceof String str && str.trim().isEmpty()) return oldValue;
        return newValue;
    }

    private MergeUtils() {}
}

