package com.rhythmix.coreservice.exception;

public class GenreAlreadyExistException extends RuntimeException {
    public GenreAlreadyExistException(String message) {
        super(message);
    }
}
