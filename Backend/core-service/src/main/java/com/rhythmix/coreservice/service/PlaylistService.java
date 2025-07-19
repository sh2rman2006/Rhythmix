package com.rhythmix.coreservice.service;

import com.rhythmix.coreservice.dto.create.PlaylistCreateDto;
import com.rhythmix.coreservice.dto.update.PlaylistUpdateDto;
import com.rhythmix.coreservice.entity.Playlist;

import java.security.Principal;
import java.util.UUID;

public interface PlaylistService {

    Playlist createPlaylist(PlaylistCreateDto playlistCreateDto, Principal principal);

    Playlist createLikedPlaylistForUser(UUID userId);

    Playlist updatePlaylist(PlaylistUpdateDto playlistUpdateDto, Principal principal);

    void deletePlaylist(UUID playlistId, Principal principal);
}
