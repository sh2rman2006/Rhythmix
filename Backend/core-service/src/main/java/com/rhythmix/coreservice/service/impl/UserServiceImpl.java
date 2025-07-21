package com.rhythmix.coreservice.service.impl;

import com.rhythmix.coreservice.entity.RhythmixUser;
import com.rhythmix.coreservice.repository.RhythmixUserRepository;
import com.rhythmix.coreservice.service.UserService;
import com.rhythmix.coreservice.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final RhythmixUserRepository rhythmixUserRepository;

    @Override
    public Optional<RhythmixUser> getUser(Principal principal) {
        return rhythmixUserRepository.findByKeycloakId(SecurityUtils.extractUserId(principal));
    }
}
