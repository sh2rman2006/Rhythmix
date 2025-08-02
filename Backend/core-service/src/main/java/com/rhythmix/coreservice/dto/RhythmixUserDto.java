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
 * DTO for {@link com.rhythmix.coreservice.entity.RhythmixUser}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class RhythmixUserDto implements Serializable {
    private UUID id;
    @NotNull
    @Size(max = 50)
    private String username;
    @NotNull
    @Size(max = 100)
    private String email;
    @Size(max = 50)
    private String firstName;
    @Size(max = 50)
    private String lastName;
    @NotNull
    private Instant createdAt;
    @NotNull
    private Instant updatedAt;
    private String avatarSeed;
    private String backgroundUrl;
}