package com.rhythmix.coreservice.repository;

import com.rhythmix.coreservice.entity.PlaylistRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlaylistRatingRepository extends JpaRepository<PlaylistRating, UUID> {
}