package com.rhythmix.coreservice.repository;

import com.rhythmix.coreservice.entity.EntityLike;
import com.rhythmix.coreservice.entity.RhythmixUser;
import com.rhythmix.coreservice.enums.LikedEntityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EntityLikeRepository extends JpaRepository<EntityLike, UUID> {

    boolean existsByEntityTypeAndEntityIdAndUser(LikedEntityType entityType, UUID entityId, RhythmixUser user);

    Optional<EntityLike> findByEntityTypeAndEntityIdAndUser(LikedEntityType entityType, UUID entityId, RhythmixUser user);

    Long countByEntityTypeAndEntityId(LikedEntityType entityType, UUID entityId);
}