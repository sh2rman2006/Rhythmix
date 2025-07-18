package com.rhythmix.coreservice.repository;

import com.rhythmix.coreservice.entity.RhythmixUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RhythmixUserRepository extends JpaRepository<RhythmixUser, UUID> {
    Optional<RhythmixUser> findByKeycloakId(UUID keycloakId);
}