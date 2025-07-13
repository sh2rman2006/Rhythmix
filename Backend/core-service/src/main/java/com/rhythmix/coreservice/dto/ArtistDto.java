package com.rhythmix.coreservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link com.rhythmix.coreservice.entity.Artist}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ArtistDto implements Serializable {
    private UUID id;
    @NotNull
    @Size(max = 255)
    private String stageName;
    @Size(max = 255)
    private String realName;
    private String biography;
    @Size(max = 100)
    private String country;
    @Size(max = 100)
    private String city;
    private String profileImageUrl;
    @NotNull
    private UUID createdBy;
    @NotNull
    private Instant createdAt;
    @NotNull
    private Instant updatedAt;
}