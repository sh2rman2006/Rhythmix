package com.rhythmix.coreservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link com.rhythmix.coreservice.entity.PlaylistTrackId}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class PlaylistTrackIdDto implements Serializable {
    @NotNull
    private UUID playlistId;
    @NotNull
    private UUID trackId;
}