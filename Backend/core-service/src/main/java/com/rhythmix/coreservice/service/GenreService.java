package com.rhythmix.coreservice.service;

import com.rhythmix.coreservice.dto.create.GenreCreateDto;
import com.rhythmix.coreservice.dto.update.GenreUpdateDto;
import com.rhythmix.coreservice.entity.Genre;

import java.util.UUID;

public interface GenreService {

    Genre createGenre(GenreCreateDto genreCreateDto);

    Genre updateGenre(GenreUpdateDto genreUpdateDto);

    void deleteGenre(UUID genreId);
}
