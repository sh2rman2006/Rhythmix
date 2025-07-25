package com.rhythmix.coreservice.enums;

import lombok.Getter;

@Getter
public enum AllowedAudioType {
    MP3("audio/mpeg", "mp3"),
    WAV("audio/wav", "wav"),
    OGG("audio/ogg", "ogg"),
    FLAC("audio/flac", "flac");

    private final String mimeType;
    private final String extension;

    AllowedAudioType(String mimeType, String extension) {
        this.mimeType = mimeType;
        this.extension = extension;
    }

    public static boolean isValid(String mimeType) {
        for (AllowedAudioType type : values()) {
            if (type.mimeType.equalsIgnoreCase(mimeType)) {
                return true;
            }
        }
        return false;
    }
}