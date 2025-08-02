package com.rhythmix.coreservice.repository;

import com.rhythmix.coreservice.entity.TrackListen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TrackListenRepository extends JpaRepository<TrackListen, UUID> {
}