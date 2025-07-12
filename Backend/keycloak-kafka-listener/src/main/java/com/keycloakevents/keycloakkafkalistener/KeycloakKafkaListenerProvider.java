package com.keycloakevents.keycloakkafkalistener;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;

import java.util.Properties;

public class KeycloakKafkaListenerProvider implements EventListenerProvider {

    private final KafkaProducer<String, KeycloakUserEvent> producer;
    private final String topic;

    public KeycloakKafkaListenerProvider(String bootstrapServers, String topic) {
        System.out.println("BOOTSTRAP SERVERS: " + bootstrapServers);
        this.topic = topic;
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", org.springframework.kafka.support.serializer.JsonSerializer.class.getName());
        this.producer = new KafkaProducer<>(props);
    }

    @Override
    public void onEvent(Event event) {
        System.out.println("РЕГИСТРАЦИЯ В KEYCLOAK, ОТПРАВКА ЧЕРЕЗ KAFKA");
        System.out.println(event);
        if (event == null || event.getType() == null) return;

        if (event.getType() == EventType.REGISTER) {
            KeycloakUserEvent payload = new KeycloakUserEvent();
            payload.setType(event.getType().toString());
            payload.setUserId(event.getUserId());
            payload.setRealmId(event.getRealmId());

            if (event.getDetails() != null) {
                payload.setEmail(event.getDetails().get("email"));
                payload.setUsername(event.getDetails().get("username"));
                payload.setFirst_name(event.getDetails().get("first_name"));
                payload.setLast_name(event.getDetails().get("last_name"));
            }

            producer.send(new ProducerRecord<>(topic, event.getUserId(), payload));
        }
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {

    }

    @Override
    public void close() {
        producer.close();
    }
}
