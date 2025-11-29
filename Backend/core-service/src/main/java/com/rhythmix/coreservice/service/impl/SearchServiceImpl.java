package com.rhythmix.coreservice.service.impl;

import com.rhythmix.coreservice.entity.Album;
import com.rhythmix.coreservice.entity.Artist;
import com.rhythmix.coreservice.entity.Track;
import com.rhythmix.coreservice.repository.AlbumRepository;
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
    private final AlbumRepository albumRepository;

    @Override
    public List<Artist> searchArtist(String name, Pageable pageable) {
        Pageable p = (pageable != null) ? pageable : Pageable.ofSize(10);
        return artistRepository.findByRealNameContainingIgnoreCaseOrStageNameContainingIgnoreCase(name, name, p);
    }

    @Override
    public List<Track> searchTrack(String name, Pageable pageable) {
        Pageable p = (pageable != null) ? pageable : Pageable.ofSize(10);
        return trackRepository.findByTitleContainingIgnoreCase(name, p);
    }

    @Override
    public List<Album> searchAlbum(String name) {
        return albumRepository.findByTitleContainingIgnoreCase(name, Pageable.ofSize(10));
    }
}
