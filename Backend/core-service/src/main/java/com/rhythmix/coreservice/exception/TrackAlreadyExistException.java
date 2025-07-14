package com.rhythmix.coreservice.exception;

public class TrackAlreadyExistException extends RuntimeException {
    public TrackAlreadyExistException(String message) {
        super(message);
    }
}
