package com.rhythmix.coreservice.entity;

import com.rhythmix.coreservice.enums.SystemPlaylistType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "playlists", schema = "public", indexes = {
        @Index(name = "idx_playlists_owner_id", columnList = "owner_id")
})
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "cover_url", length = Integer.MAX_VALUE)
    private String coverUrl;

    @Column(name = "cover_file", length = Integer.MAX_VALUE)
    private String coverFile;

    @NotNull
    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @NotNull
    @ColumnDefault("true")
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = false;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "is_system", nullable = false)
    private Boolean isSystem = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "system_type")
    private SystemPlaylistType systemType;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "playlist")
    private Set<PlaylistTrack> playlistTracks = new LinkedHashSet<>();

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "name = " + name + ", " +
                "description = " + description + ", " +
                "coverUrl = " + coverUrl + ", " +
                "coverFile = " + coverFile + ", " +
                "ownerId = " + ownerId + ", " +
                "isPublic = " + isPublic + ", " +
                "isSystem = " + isSystem + ", " +
                "systemType = " + systemType + ", " +
                "createdAt = " + createdAt + ", " +
                "updatedAt = " + updatedAt + ")";
    }
}