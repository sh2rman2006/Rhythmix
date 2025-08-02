package com.rhythmix.coreservice.dto.create;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ListenTrackDto(@NotNull UUID trackId) {
}
