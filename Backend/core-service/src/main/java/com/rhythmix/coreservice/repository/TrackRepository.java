package com.rhythmix.coreservice.repository;

import com.rhythmix.coreservice.entity.Track;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrackRepository extends JpaRepository<Track, UUID> {

    boolean existsByTitleIgnoreCase(String title);

    List<Track> findAllByTitleIgnoreCase(String title);

    @EntityGraph(attributePaths = {"artist", "album"})
    Optional<Track> findWithArtistAndAlbumById(UUID id);

    @EntityGraph(attributePaths = {"artist", "album", "genres"})
    Optional<Track> findWithRelationsById(UUID id);

    List<Track> findTop20ByOrderByTotalListensDesc();

    @EntityGraph(attributePaths = {"artist", "album", "genres"})
    List<Track> findWithRelationsByIdIn(Collection<UUID> id);

    @EntityGraph(attributePaths = {"genres"})
    @Query("select t from Track t")
    List<Track> findAllWithGenres();

    @EntityGraph(attributePaths = {"artist", "album", "genres"})
    @Query("select t from Track t")
    List<Track> findAllWithRelations();

}