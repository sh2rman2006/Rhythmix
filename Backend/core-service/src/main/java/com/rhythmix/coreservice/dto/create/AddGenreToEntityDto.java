package com.rhythmix.coreservice.dto.create;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public record AddGenreToEntityDto(@NotNull @NotEmpty List<UUID> genreIds, @NotNull UUID entityId) implements Serializable {
}