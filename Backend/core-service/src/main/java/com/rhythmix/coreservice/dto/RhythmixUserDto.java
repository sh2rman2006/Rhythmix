package com.rhythmix.coreservice.dto;

import com.rhythmix.coreservice.entity.RhythmixUser;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
 * DTO for {@link RhythmixUser}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class RhythmixUserDto implements Serializable {
    private UUID id;
    @NotNull
    @Size(max = 50)
    @NotBlank
    private String username;
    @NotNull
    @Size(max = 100)
    @Email
    private String email;
    @Size(max = 50)
    private String firstName;
    @Size(max = 50)
    private String lastName;
    @NotNull
    private Instant createdAt;
    @NotNull
    private Instant updatedAt;
}