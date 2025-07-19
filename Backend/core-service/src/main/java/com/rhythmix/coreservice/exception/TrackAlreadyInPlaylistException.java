package com.rhythmix.coreservice.exception;

public class TrackAlreadyInPlaylistException extends RuntimeException {
    public TrackAlreadyInPlaylistException(String message) {
        super(message);
    }
}
