package com.rhythmix.coreservice.service;

import com.rhythmix.coreservice.dto.create.TrackCreateDto;
import com.rhythmix.coreservice.entity.Track;

import java.io.IOException;
import java.security.Principal;

public interface TrackService {

    Track createTrack(TrackCreateDto trackCreateDto, Principal principal) throws IOException;
}
