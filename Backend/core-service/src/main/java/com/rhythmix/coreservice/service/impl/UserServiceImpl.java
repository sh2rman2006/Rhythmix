package com.rhythmix.coreservice.service.impl;

import com.rhythmix.coreservice.entity.RhythmixUser;
import com.rhythmix.coreservice.repository.RhythmixUserRepository;
import com.rhythmix.coreservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final RhythmixUserRepository rhythmixUserRepository;

    private UUID extractUserId(Principal principal) {
        var token = (JwtAuthenticationToken) principal;
        Jwt jwt = (Jwt) token.getPrincipal();
        return UUID.fromString(jwt.getSubject());
    }

    @Override
    public Optional<RhythmixUser> getUser(Principal principal) {
        return rhythmixUserRepository.findById(extractUserId(principal));
    }
}
