package com.rhythmix.coreservice.entity;

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
@Table(name = "artists", schema = "public")
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 255)
    @NotNull
    @Column(name = "stage_name", nullable = false)
    private String stageName;

    @Size(max = 255)
    @Column(name = "real_name")
    private String realName;

    @Column(name = "biography", length = Integer.MAX_VALUE)
    private String biography;

    @Size(max = 100)
    @Column(name = "country", length = 100)
    private String country;

    @Size(max = 100)
    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "profile_image_url", length = Integer.MAX_VALUE)
    private String profileImageUrl;

    @Column(name = "file_image_url", length = Integer.MAX_VALUE)
    private String fileImageUrl;

    @NotNull
    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "artist")
    private Set<Album> albums = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(
            name = "artist_genres",
            joinColumns = @JoinColumn(name = "artist_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new LinkedHashSet<>();

    @OneToMany(mappedBy = "artist")
    private Set<Track> tracks = new LinkedHashSet<>();

    @Override
    public String toString() {
        return "Artist{" +
                "id=" + id +
                ", stageName='" + stageName + '\'' +
                ", realName='" + realName + '\'' +
                ", biography='" + biography + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", fileImageUrl='" + fileImageUrl + '\'' +
                ", createdBy=" + createdBy +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}