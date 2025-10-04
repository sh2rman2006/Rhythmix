package com.rhythmix.coreservice.repository;

import com.rhythmix.coreservice.entity.Artist;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, UUID> {

    List<Artist> findAllByStageNameContainingIgnoreCase(String stageName);

    boolean existsByStageNameIgnoreCase(String stageName);

    @EntityGraph(attributePaths = {"genres"})
    Optional<Artist> findWithGenresById(UUID id);

    List<Artist> findByRealNameContainingIgnoreCaseOrStageNameContainingIgnoreCase(@Size(max = 255) String realName, @Size(max = 255) @NotNull String stageName, Pageable pageable);
}