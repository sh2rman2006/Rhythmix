package com.rhythmix.coreservice.exception;

public class TrackNotFoundException extends RuntimeException {
    public TrackNotFoundException(String message) {
        super(message);
    }
}
