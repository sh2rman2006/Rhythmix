package com.rhythmix.coreservice.service.impl;

import com.rhythmix.coreservice.dto.create.AlbumCreateDto;
import com.rhythmix.coreservice.entity.Album;
import com.rhythmix.coreservice.repository.AlbumRepository;
import com.rhythmix.coreservice.service.AlbumService;
import com.rhythmix.coreservice.service.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {
    private final AlbumRepository albumRepository;
    private final MinioService minioService;

//    todo: implement createAlbum method

    @Override
    public Optional<Album> getAlbumById(UUID id) {
        return albumRepository.findById(id);
    }

    @Override
    public List<Album> getAlbumByTitle(String title) {
        return albumRepository.findByTitleIgnoreCase(title);
    }

    @Override
    public Album createAlbum(AlbumCreateDto albumCreateDto) {
        return null;
    }
}
