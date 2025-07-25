package com.rhythmix.coreservice.repository;

import com.rhythmix.coreservice.entity.Playlist;
import com.rhythmix.coreservice.enums.SystemPlaylistType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {

    boolean existsByNameIgnoreCase(String name);
    
    Optional<Playlist> findByOwnerIdAndSystemType(UUID ownerId, SystemPlaylistType systemType);
}