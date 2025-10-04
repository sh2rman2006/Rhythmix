package com.rhythmix.coreservice.service;

import com.rhythmix.coreservice.entity.Artist;
import com.rhythmix.coreservice.entity.Track;

import java.util.List;

public interface SearchService {
    public List<Artist> searchArtist(String name);

    public List<Track> searchTrack(String name);
}
