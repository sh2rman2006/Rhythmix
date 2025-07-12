package com.rhythmix.coreservice.dto;

import lombok.Data;

@Data
public class KeycloakUserEvent {
    private String type;
    private String userId;
    private String realmId;
    private String email;
    private String username;
    private String first_name;
    private String last_name;

}
