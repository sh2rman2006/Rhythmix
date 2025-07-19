package com.rhythmix.coreservice.service.impl;

import com.rhythmix.coreservice.dto.create.GenreCreateDto;
import com.rhythmix.coreservice.entity.Genre;
import com.rhythmix.coreservice.exception.GenreAlreadyExistException;
import com.rhythmix.coreservice.exception.GenreNotFoundException;
import com.rhythmix.coreservice.repository.GenreRepository;
import com.rhythmix.coreservice.service.GenreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;


    @Override
    public Genre createGenre(GenreCreateDto genreCreateDto) {

        if (genreRepository.existsByNameIgnoreCase(genreCreateDto.name())) {
            throw new GenreAlreadyExistException("Genre with name '" + genreCreateDto.name() + "' already exists.");
        }

        Genre genre = null;
        if (genreCreateDto.parentId() != null) {
            genre = genreRepository.findById(genreCreateDto.parentId())
                    .orElseThrow(() -> new GenreNotFoundException("Parent genre not found with ID: " + genreCreateDto.parentId()));
        }

        Genre savedGenre = genreRepository.save(
                Genre.builder()
                .name(genreCreateDto.name())
                .description(genreCreateDto.description())
                .parent(genre)
                .build()
        );

        log.info("Created genre: {}", savedGenre);
        return savedGenre;
    }
}
