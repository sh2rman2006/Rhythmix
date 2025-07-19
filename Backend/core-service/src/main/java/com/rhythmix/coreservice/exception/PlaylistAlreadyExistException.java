package com.rhythmix.coreservice.exception;

public class PlaylistAlreadyExistException extends RuntimeException {
    public PlaylistAlreadyExistException(String message) {
        super(message);
    }
}
