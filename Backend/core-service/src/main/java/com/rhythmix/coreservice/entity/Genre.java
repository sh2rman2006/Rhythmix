package com.rhythmix.coreservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.LinkedHashSet;
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

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "name = " + name + ", " +
                "description = " + description + ")";
    }
}