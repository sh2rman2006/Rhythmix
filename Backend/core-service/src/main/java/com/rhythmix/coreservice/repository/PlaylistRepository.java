package com.rhythmix.coreservice.repository;

import com.rhythmix.coreservice.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {

    boolean existsByNameIgnoreCase(String name);
}