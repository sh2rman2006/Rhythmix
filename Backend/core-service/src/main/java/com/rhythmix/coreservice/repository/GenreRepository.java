package com.rhythmix.coreservice.repository;

import com.rhythmix.coreservice.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GenreRepository extends JpaRepository<Genre, UUID> {
    boolean existsByNameIgnoreCase(String name);
}