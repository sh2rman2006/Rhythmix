package com.rhythmix.coreservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for {@link com.rhythmix.coreservice.entity.Track}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class TrackDto implements Serializable {
    private UUID id;
    @NotNull
    @Size(max = 255)
    private String title;
    private String description;
    @NotNull
    private String audioUrl;
    private String coverUrl;
    private Integer duration;
    @NotNull
    private Boolean explicit = false;
    private LocalDate releaseDate;
    @NotNull
    private Instant uploadedAt;
    @NotNull
    private ArtistDto artist;
    private AlbumDto album;
}