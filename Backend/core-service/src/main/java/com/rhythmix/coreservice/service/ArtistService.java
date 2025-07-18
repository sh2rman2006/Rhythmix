package com.rhythmix.coreservice.service;

import com.rhythmix.coreservice.dto.create.ArtistCreateDto;
import com.rhythmix.coreservice.dto.update.ArtistUpdateDto;
import com.rhythmix.coreservice.entity.Artist;

import java.io.IOException;
import java.security.Principal;

public interface ArtistService {

    Artist createArtist(ArtistCreateDto artistCreateDto, Principal principal) throws IOException;

    Artist updateArtist(ArtistUpdateDto artistUpdateDto) throws IOException;
}
