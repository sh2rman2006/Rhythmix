package com.rhythmix.coreservice.exception;

public class PlaylistTrackNotFoundException extends RuntimeException {
  public PlaylistTrackNotFoundException(String message) {
    super(message);
  }
}
