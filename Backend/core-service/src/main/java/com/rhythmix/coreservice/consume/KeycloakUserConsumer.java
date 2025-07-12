package com.rhythmix.coreservice.consume;

import com.rhythmix.coreservice.dto.KeycloakUserEvent;
import com.rhythmix.coreservice.entity.RhythmixUser;
import com.rhythmix.coreservice.mapper.RhythmixUserMapper;
import com.rhythmix.coreservice.repository.RhythmixUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class KeycloakUserConsumer {
    private final RhythmixUserRepository userRepository;
    private final RhythmixUserMapper userMapper;

    @KafkaListener(topics = "keycloak-events", groupId = "core-group", containerFactory = "kafkaListenerContainerFactory")
    public void listen(KeycloakUserEvent event) {
        log.info("Received keycloak event: {}", event);
        try {
            RhythmixUser user = userRepository.save(userMapper.toEntity(event));
            log.info("Successfully saved user: {}", user);
        } catch (Exception e) {
            log.error("Failed to process keycloak event, event was send to DLT queue: {}", event, e);
            throw e;
        }
    }
}
