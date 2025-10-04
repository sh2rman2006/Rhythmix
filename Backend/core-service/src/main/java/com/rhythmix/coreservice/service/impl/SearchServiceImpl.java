package com.rhythmix.coreservice.service.impl;

import com.rhythmix.coreservice.entity.Artist;
import com.rhythmix.coreservice.entity.Track;
import com.rhythmix.coreservice.repository.ArtistRepository;
import com.rhythmix.coreservice.repository.TrackRepository;
import com.rhythmix.coreservice.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final ArtistRepository artistRepository;
    private final TrackRepository trackRepository;

    @Override
    public List<Artist> searchArtist(String name) {
        return artistRepository.findByRealNameContainingIgnoreCaseOrStageNameContainingIgnoreCase(name, name, Pageable.ofSize(10));
    }

    @Override
    public List<Track> searchTrack(String name) {
        return trackRepository.findByTitleContainingIgnoreCase(name, Pageable.ofSize(10));
    }
}
