package com.rhythmix.coreservice.repository;

import com.rhythmix.coreservice.entity.EntityLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EntityLikeRepository extends JpaRepository<EntityLike, UUID> {
}