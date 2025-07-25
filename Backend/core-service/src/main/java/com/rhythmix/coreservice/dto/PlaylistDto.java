package com.rhythmix.coreservice.dto;

import com.rhythmix.coreservice.enums.SystemPlaylistType;
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
 * DTO for {@link com.rhythmix.coreservice.entity.Playlist}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class PlaylistDto implements Serializable {
    private UUID id;
    @NotNull
    @Size(max = 255)
    private String name;
    private String description;
    private String coverUrl;
    @NotNull
    private UUID ownerId;
    @NotNull
    private Boolean isPublic = false;
    @NotNull
    private Boolean isSystem = false;
    private SystemPlaylistType systemPlaylistType;
    @NotNull
    private Instant createdAt;
    @NotNull
    private Instant updatedAt;
}