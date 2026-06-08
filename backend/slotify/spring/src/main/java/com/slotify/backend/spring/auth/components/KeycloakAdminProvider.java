package com.slotify.backend.spring.auth.components;

import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeycloakAdminProvider {

    @Value("${keycloak.server-url}")
    private String serverUrl;

    public Keycloak getInstance() {

        // TODO: REFACTORIZAR EL HARDCODE
        return Keycloak.getInstance(
                serverUrl,
                "master",
                "admin",
                "admin",
                "admin-cli"
        );
    }
}
