package com.rhythmix.coreservice.service;

import com.rhythmix.coreservice.dto.create.AlbumCreateDto;
import com.rhythmix.coreservice.entity.Album;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlbumService {

    Optional<Album> getAlbumById(UUID id);

    List<Album> getAlbumByTitle(String title);

    Album createAlbum(AlbumCreateDto albumCreateDto);

}
