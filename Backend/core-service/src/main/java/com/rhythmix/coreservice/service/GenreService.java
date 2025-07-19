package com.rhythmix.coreservice.service;

import com.rhythmix.coreservice.dto.create.GenreCreateDto;
import com.rhythmix.coreservice.entity.Genre;

public interface GenreService {

    Genre createGenre(GenreCreateDto genreCreateDto);
}
