package com.rhythmix.coreservice.exception;

public class TrackNotInAlbumException extends RuntimeException {
    public TrackNotInAlbumException(String message) {
        super(message);
    }
}
