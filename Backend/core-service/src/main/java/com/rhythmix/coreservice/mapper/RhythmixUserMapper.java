package com.rhythmix.coreservice.mapper;

import com.rhythmix.coreservice.dto.KeycloakUserEvent;
import com.rhythmix.coreservice.dto.RhythmixUserDto;
import com.rhythmix.coreservice.entity.RhythmixUser;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RhythmixUserMapper implements EntitiesMapper<RhythmixUser, RhythmixUserDto> {
    private final CoverUrlResolver coverUrlResolver;

    @Override
    public RhythmixUserDto toDto(RhythmixUser rhythmixUser) {
        return new RhythmixUserDto(
                rhythmixUser.getId(),
                rhythmixUser.getUsername(),
                rhythmixUser.getEmail(),
                rhythmixUser.getFirstName(),
                rhythmixUser.getLastName(),
                rhythmixUser.getCreatedAt(),
                rhythmixUser.getUpdatedAt(),
                coverUrlResolver.resolve(rhythmixUser.getAvatarSeed()),
                coverUrlResolver.resolve(rhythmixUser.getBackgroundUrl())
        );
    }

    @Override
    public RhythmixUser toEntity(RhythmixUserDto rhythmixUserDto) {
        return RhythmixUser.builder()
                .id(rhythmixUserDto.getId())
                .username(rhythmixUserDto.getUsername())
                .email(rhythmixUserDto.getEmail())
                .firstName(rhythmixUserDto.getFirstName())
                .lastName(rhythmixUserDto.getLastName())
                .createdAt(rhythmixUserDto.getCreatedAt())
                .updatedAt(rhythmixUserDto.getUpdatedAt())
                .build();
    }

    public RhythmixUser toEntity(@NotNull KeycloakUserEvent keycloakUserEvent) {
        if (keycloakUserEvent.getUserId() == null ||
                keycloakUserEvent.getUsername() == null || keycloakUserEvent.getEmail() == null) {
            throw new IllegalArgumentException("Invalid keycloak user event");
        }

        Instant now = Instant.now();
        return RhythmixUser.builder()
                .keycloakId(UUID.fromString(keycloakUserEvent.getUserId()))
                .username(keycloakUserEvent.getUsername())
                .email(keycloakUserEvent.getEmail())
                .firstName(keycloakUserEvent.getFirst_name())
                .lastName(keycloakUserEvent.getLast_name())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

}
