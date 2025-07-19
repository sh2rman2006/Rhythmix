package com.rhythmix.coreservice.dto.update;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link com.rhythmix.coreservice.entity.Genre}
 */
public record GenreUpdateDto(@NotNull UUID id,
                             @Size(max = 100) String name,
                             @Size(max = 100) String description,
                             UUID parentId) implements Serializable {
}