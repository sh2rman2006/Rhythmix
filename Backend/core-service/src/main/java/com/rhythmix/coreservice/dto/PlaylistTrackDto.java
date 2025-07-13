package com.rhythmix.coreservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link com.rhythmix.coreservice.entity.PlaylistTrack}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class PlaylistTrackDto implements Serializable {
    @NotNull
    private PlaylistTrackIdDto id;
    @NotNull
    private TrackDto track;
    @NotNull
    private Instant addedAt;
}