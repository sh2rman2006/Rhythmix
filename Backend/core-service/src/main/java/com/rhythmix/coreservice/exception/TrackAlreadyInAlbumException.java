package com.rhythmix.coreservice.exception;

public class TrackAlreadyInAlbumException extends RuntimeException {
    public TrackAlreadyInAlbumException(String message) {
        super(message);
    }
}
