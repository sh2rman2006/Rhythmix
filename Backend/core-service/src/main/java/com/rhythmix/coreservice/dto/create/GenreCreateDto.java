package com.rhythmix.coreservice.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link com.rhythmix.coreservice.entity.Genre}
 */
public record GenreCreateDto(
        @NotNull @Size(max = 100) @NotBlank String name,
        @Size(max = 100) String description,
        UUID parentId) implements Serializable {
}