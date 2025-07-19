package com.rhythmix.coreservice.exception;

public class PlaylistAccessDeniedException extends RuntimeException {
    public PlaylistAccessDeniedException(String message) {
        super(message);
    }
}
