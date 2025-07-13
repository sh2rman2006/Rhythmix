package com.rhythmix.coreservice.mapper;

import com.rhythmix.coreservice.dto.GenreDto;
import com.rhythmix.coreservice.entity.Genre;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper implements EntitiesMapper<Genre, GenreDto> {
    @Override
    public GenreDto toDto(Genre genre) {
        return new GenreDto(
                genre.getId(),
                genre.getName(),
                genre.getDescription()
        );
    }

    @Override
    public Genre toEntity(GenreDto genreDto) {
        return Genre.builder()
                .id(genreDto.getId())
                .name(genreDto.getName())
                .description(genreDto.getDescription())
                .build();
    }
}
