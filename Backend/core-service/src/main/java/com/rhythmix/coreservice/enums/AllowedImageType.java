package com.rhythmix.coreservice.enums;

import lombok.Getter;

@Getter
public enum AllowedImageType {
    JPEG("image/jpeg", "jpeg"),
    PNG("image/png", "png"),
    WEBP("image/webp", "webp"),
    GIF("image/gif", "gif");

    private final String mimeType;
    private final String extension;

    AllowedImageType(String mimeType, String extension) {
        this.mimeType = mimeType;
        this.extension = extension;
    }

    public static boolean isValid(String mimeType) {
        for (AllowedImageType type : values()) {
            if (type.mimeType.equalsIgnoreCase(mimeType)) {
                return true;
            }
        }
        return false;
    }
}
