package com.rhythmix.coreservice.service;

import com.rhythmix.coreservice.dto.create.AlbumCreateDto;
import com.rhythmix.coreservice.dto.update.AlbumUpdateDto;
import com.rhythmix.coreservice.entity.Album;

import java.util.UUID;

public interface AlbumService {

    Album createAlbum(AlbumCreateDto albumCreateDto);

    Album updateAlbum(AlbumUpdateDto albumUpdateDto);

    void deleteAlbum(UUID albumId);

}
