package com.keycloakevents.keycloakkafkalistener;

import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class KeycloakKafkaListenerProviderFactory implements EventListenerProviderFactory {

    private String topic;
    private String bootstrapServers;

    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {
        return new KeycloakKafkaListenerProvider(bootstrapServers, topic);
    }

    @Override
    public void init(org.keycloak.Config.Scope scope) {
        this.bootstrapServers = scope.get("bootstrap.servers", "kafka.rhythmix-dev.svc:9092");
        this.topic = scope.get("topic", "keycloak-events");
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return "kafka";
    }
}
