package com.rhythmix.coreservice.service;

import com.rhythmix.coreservice.entity.Album;
import com.rhythmix.coreservice.entity.Artist;
import com.rhythmix.coreservice.entity.Track;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SearchService {
    List<Artist> searchArtist(String name, Pageable pageable);

    List<Track> searchTrack(String name, Pageable pageable);

    List<Album> searchAlbum(String name);

    default List<Track> searchTrack(String name) {
        return searchTrack(name, Pageable.ofSize(10));
    }

    default List<Artist> searchArtist(String name) {
        return searchArtist(name, Pageable.ofSize(10));
    }
}
