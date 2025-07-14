package com.rhythmix.coreservice.repository;

import com.rhythmix.coreservice.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlbumRepository extends JpaRepository<Album, UUID> {

    List<Album> findByTitleIgnoreCase(String title);
}