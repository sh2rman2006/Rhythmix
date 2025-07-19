package com.rhythmix.coreservice.service.impl;

import com.rhythmix.coreservice.dto.create.GenreCreateDto;
import com.rhythmix.coreservice.dto.update.GenreUpdateDto;
import com.rhythmix.coreservice.entity.Genre;
import com.rhythmix.coreservice.exception.GenreAlreadyExistException;
import com.rhythmix.coreservice.exception.GenreNotFoundException;
import com.rhythmix.coreservice.repository.GenreRepository;
import com.rhythmix.coreservice.service.GenreService;
import com.rhythmix.coreservice.utils.MergeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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

        Genre parent = null;
        if (genreCreateDto.parentId() != null) {
            parent = genreRepository.findById(genreCreateDto.parentId())
                    .orElseThrow(() -> new GenreNotFoundException("Parent genre not found with ID: " + genreCreateDto.parentId()));
        }

        Genre savedGenre = genreRepository.save(
                Genre.builder()
                .name(genreCreateDto.name())
                .description(genreCreateDto.description())
                .parent(parent)
                .build()
        );

        log.info("Created genre: {}", savedGenre);
        return savedGenre;
    }

    @Override
    public Genre updateGenre(GenreUpdateDto genreUpdateDto) {

        Genre genre = genreRepository.findById(genreUpdateDto.id()).orElseThrow(
                () -> new GenreNotFoundException("Genre not found with ID: " + genreUpdateDto.id())
        );

        Genre parent = null;
        if (genreUpdateDto.parentId() != null) {
            parent = genreRepository.findById(genreUpdateDto.parentId())
                    .orElseThrow(() -> new GenreNotFoundException("Parent genre not found with ID: " + genreUpdateDto.parentId()));
        }

        genre.setName(MergeUtils.preferNewIfPresent(genre.getName(), genreUpdateDto.name()));
        genre.setDescription(MergeUtils.preferNewIfPresent(genre.getDescription(), genreUpdateDto.description()));
        genre.setParent(MergeUtils.preferNewIfPresent(genre.getParent(), parent));

        Genre updatedGenre = genreRepository.save(genre);
        log.info("Updated genre: {}", updatedGenre);
        return updatedGenre;
    }

    @Override
    @Transactional
    public void deleteGenre(UUID genreId) {
        Genre genre = genreRepository.findById(genreId).orElseThrow(
                () -> new GenreNotFoundException("Genre not found with ID: " + genreId)
        );
        genreRepository.delete(genre);
        log.info("Deleted genre: {}", genre);
    }

}
