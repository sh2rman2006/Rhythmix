package com.rhythmix.coreservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tracks", schema = "public", indexes = {
        @Index(name = "idx_tracks_artist_id", columnList = "artist_id"),
        @Index(name = "idx_tracks_album_id", columnList = "album_id")
})
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 255)
    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @NotNull
    @Column(name = "audio_file", nullable = false, length = Integer.MAX_VALUE)
    private String audioFile;

    @Column(name = "cover_url", length = Integer.MAX_VALUE)
    private String coverUrl;

    @Column(name = "cover_file", length = Integer.MAX_VALUE)
    private String coverFile;

    @Column(name = "duration")
    private Integer duration;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "explicit", nullable = false)
    private Boolean explicit = false;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt;

    @NotNull
    @Column(name = "uploaded_by", nullable = false)
    private UUID uploadedBy;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "album_id")
    private Album album;

    @OneToMany(mappedBy = "track")
    private Set<PlaylistTrack> playlistTracks = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(
            name = "track_genres",
            joinColumns = @JoinColumn(name = "track_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new LinkedHashSet<>();

    @Override
    public String toString() {
        return "Track{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", audioFile='" + audioFile + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                ", coverFile='" + coverFile + '\'' +
                ", duration=" + duration +
                ", explicit=" + explicit +
                ", releaseDate=" + releaseDate +
                ", uploadedAt=" + uploadedAt +
                ", uploadedBy=" + uploadedBy +
                '}';
    }
}