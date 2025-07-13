package com.rhythmix.coreservice.service;

import com.rhythmix.coreservice.dto.create.ArtistCreateDto;
import com.rhythmix.coreservice.entity.Artist;

import java.io.IOException;
import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

public interface ArtistService {

    Optional<Artist> getArtistById(UUID id);

    Optional<Artist> getArtistByStageName(String stageName);

    Artist createArtist(ArtistCreateDto artistCreateDto, Principal principal) throws IOException;
}
