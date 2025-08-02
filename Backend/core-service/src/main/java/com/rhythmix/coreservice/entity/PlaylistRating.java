package com.rhythmix.coreservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "playlist_ratings", schema = "public", indexes = {
        @Index(name = "idx_ratings_playlist", columnList = "playlist_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uq_playlist_rating", columnNames = {"playlist_id", "user_id"})
})
public class PlaylistRating {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private RhythmixUser user;

    @NotNull
    @Column(name = "rating", nullable = false)
    private Integer rating;

    @ColumnDefault("now()")
    @Column(name = "created_at")
    private Instant createdAt;

}