package com.rhythmix.coreservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.proxy.HibernateProxy;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "genres", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "genres_name_key", columnNames = {"name"})
})
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "parent_id")
    private Genre parent;

    @ManyToMany(mappedBy = "genres")
    private Set<Album> albums = new LinkedHashSet<>();

    @ManyToMany(mappedBy = "genres")
    private Set<Artist> artists = new LinkedHashSet<>();

    @OneToMany(mappedBy = "parent")
    private Set<Genre> genres = new LinkedHashSet<>();

    @ManyToMany(mappedBy = "genres")
    private Set<Track> tracks = new LinkedHashSet<>();

    public void addArtist(Artist artist) {
        if (this.artists.contains(artist)) return;
        this.artists.add(artist);
        artist.getGenres().add(this);
    }

    public void addTrack(Track track) {
        if (this.tracks.contains(track)) return;
        this.tracks.add(track);
        track.getGenres().add(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "name = " + name + ", " +
                "description = " + description + ")";
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Genre genre = (Genre) o;
        return getId() != null && Objects.equals(getId(), genre.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}