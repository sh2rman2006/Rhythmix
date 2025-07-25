package com.rhythmix.coreservice.service;

import com.rhythmix.coreservice.dto.create.AddTrackToPlaylistDto;
import com.rhythmix.coreservice.dto.create.PlaylistCreateDto;
import com.rhythmix.coreservice.dto.update.PlaylistUpdateDto;
import com.rhythmix.coreservice.entity.Playlist;
import com.rhythmix.coreservice.entity.PlaylistTrack;

import java.security.Principal;
import java.util.UUID;

public interface PlaylistService {

    Playlist createPlaylist(PlaylistCreateDto playlistCreateDto, Principal principal);

    Playlist createLikedPlaylistForUser(UUID userId);

    Playlist updatePlaylist(PlaylistUpdateDto playlistUpdateDto, Principal principal);

    void deletePlaylist(UUID playlistId, Principal principal);

    PlaylistTrack addTrackToPlaylist(AddTrackToPlaylistDto addTrackToPlaylistDto, Principal principal);

    void deleteTrackFromPlaylist(UUID playlistId, UUID trackId, Principal principal);

    void likeTrack(UUID trackId, Principal principal);

    void unlikeTrack(UUID trackId, Principal principal);

    boolean isLiked(UUID trackId, Principal principal);
}
