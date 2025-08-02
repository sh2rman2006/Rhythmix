package com.rhythmix.coreservice.service;

import com.rhythmix.coreservice.dto.create.AlbumCreateDto;
import com.rhythmix.coreservice.dto.update.AlbumUpdateDto;
import com.rhythmix.coreservice.entity.Album;

import java.security.Principal;
import java.util.UUID;

public interface AlbumService {

    Album createAlbum(AlbumCreateDto albumCreateDto);

    Album updateAlbum(AlbumUpdateDto albumUpdateDto);

    void deleteAlbum(UUID albumId);

    Album addTrackToAlbum(UUID trackId, UUID albumId);

    void removeTrackFromAlbum(UUID trackId, UUID albumId);

    void likeAlbum(UUID albumId, Principal principal);

    void unlikeAlbum(UUID albumId, Principal principal);

    boolean isLiked(UUID albumId, Principal principal);
}
