package com.rhythmix.coreservice.dto.create;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.UUID;

public record AddTrackToAlbumDto(
        @NotNull(message = "albumId не должен быть null") UUID albumId,
        @NotNull(message = "trackId не должен быть null") UUID trackId
) implements Serializable {
}
