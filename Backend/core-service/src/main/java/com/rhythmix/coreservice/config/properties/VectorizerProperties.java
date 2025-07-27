package com.rhythmix.coreservice.config.properties;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "rhythmix.vectorizer")
@Getter
@Setter
public class VectorizerProperties {
    private int dimension = 32;
    private float decayFactor = 0.7f;
    private Duration vectorTtl = Duration.ofHours(6);

    @PostConstruct
    public void validateParams() {
        if (dimension <= 0) {
            throw new IllegalStateException("Vector dimension must be positive");
        }
        if (decayFactor <= 0 || decayFactor >= 1) {
            throw new IllegalStateException("Decay factor must be in range (0, 1)");
        }
    }
}
