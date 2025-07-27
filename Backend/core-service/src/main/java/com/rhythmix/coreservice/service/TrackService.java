package com.rhythmix.coreservice.service;

import com.rhythmix.coreservice.dto.create.AddGenreToEntityDto;
import com.rhythmix.coreservice.dto.create.TrackCreateDto;
import com.rhythmix.coreservice.dto.update.TrackUpdateDto;
import com.rhythmix.coreservice.entity.Track;

import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

public interface TrackService {

    Track createTrack(TrackCreateDto trackCreateDto, Principal principal) throws IOException;

    Track updateTrack(TrackUpdateDto trackUpdateDto);

    void deleteTrack(UUID trackId);

    Track addGenreToTrack(AddGenreToEntityDto addGenreToEntityDto);

    Track removeGenreFromTrack(UUID trackGenreId, UUID trackId);

    long countLikes(UUID trackId);

    boolean isLiked(UUID trackId, Principal principal);
}
