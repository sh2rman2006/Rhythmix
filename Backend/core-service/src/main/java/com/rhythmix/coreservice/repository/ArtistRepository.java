package com.rhythmix.coreservice.repository;

import com.rhythmix.coreservice.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, UUID> {

    List<Artist> findByStageNameIgnoreCase(String stageName);

    boolean existsByStageNameIgnoreCase(String stageName);
}