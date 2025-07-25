package com.rhythmix.coreservice.dto.create;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link com.rhythmix.coreservice.entity.EntityLike}
 */
public record AddEntityLikeDto(@NotNull UUID entityId) implements Serializable {
}