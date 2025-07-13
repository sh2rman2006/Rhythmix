package com.rhythmix.coreservice.repository;

import com.rhythmix.coreservice.entity.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TrackRepository extends JpaRepository<Track, UUID> {
}