package com.rhythmix.coreservice.exception;

public class ArtistAlreadyExistException extends RuntimeException {
    public ArtistAlreadyExistException(String message) {
        super(message);
    }
}
