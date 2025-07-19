package com.rhythmix.coreservice.service;

import com.rhythmix.coreservice.dto.create.PlaylistCreateDto;
import com.rhythmix.coreservice.entity.Playlist;

import java.security.Principal;

public interface PlaylistService {

    Playlist createPlaylist(PlaylistCreateDto playlistCreateDto, Principal principal);
}
