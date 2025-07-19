package com.rhythmix.coreservice.dto.create;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link com.rhythmix.coreservice.entity.PlaylistTrack}
 */
public record AddTrackToPlaylistDto(
        @NotNull(message = "playlistId не должен быть null") UUID playlistId,
        @NotNull(message = "trackId не должен быть null") UUID trackId)
        implements Serializable {}