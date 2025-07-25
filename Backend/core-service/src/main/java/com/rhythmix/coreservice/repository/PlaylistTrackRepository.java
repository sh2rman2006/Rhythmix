package com.rhythmix.coreservice.repository;

import com.rhythmix.coreservice.entity.Playlist;
import com.rhythmix.coreservice.entity.PlaylistTrack;
import com.rhythmix.coreservice.entity.PlaylistTrackId;
import com.rhythmix.coreservice.entity.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlaylistTrackRepository extends JpaRepository<PlaylistTrack, PlaylistTrackId> {
    boolean existsByPlaylistAndTrack(Playlist playlist, Track track);

    boolean existsByPlaylistIdAndTrackId(UUID playlistId, UUID trackId);
}