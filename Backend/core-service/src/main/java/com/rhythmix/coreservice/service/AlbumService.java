package com.rhythmix.coreservice.service;

import com.rhythmix.coreservice.dto.create.AlbumCreateDto;
import com.rhythmix.coreservice.entity.Album;

public interface AlbumService {

    Album createAlbum(AlbumCreateDto albumCreateDto);

}
