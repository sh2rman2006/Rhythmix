package com.rhythmix.coreservice.exception;

public class AlbumAlreadyExistException extends RuntimeException {
    public AlbumAlreadyExistException(String message) {
        super(message);
    }
}
