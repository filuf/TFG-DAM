package com.slotify.backend.spring.auth.services;

import com.slotify.backend.spring.auth.components.KeycloakAdminProvider;
import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.exceptions.AlreadyExistsException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakServiceImpl implements KeycloakService {

    @Value("${keycloak.realm}")
    private String realm;

    private final KeycloakAdminProvider keycloakAdminProvider;


    @Override
    public UUID createUser(String username, String email, String password, AccountType accountType) {
        Keycloak keycloak = keycloakAdminProvider.getInstance();

        // keycloak no soporta usuarios con espacios sin una config extra y por falta de tiempo se hace este ajuste
        username = String.join("_", username.split(" "));

        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(email);
        user.setEnabled(true);
        user.setEmailVerified(true); // cambiar si queremos verificacion manual del email
        user.setAttributes(Map.of(
                "account-type", List.of(accountType.getType())
        ));

        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(password);

        user.setCredentials(List.of(passwordCred));

        try (Response response = keycloak.realm(realm)
                .users()
                .create(user)) {

            if (response.getStatus() == 409) {
                String errorBody = response.readEntity(String.class);
                log.warn("Conflicto al crear usuario en Keycloak: {}", errorBody);
                throw new AlreadyExistsException("El usuario ya existe en el sistema de identidad: " + errorBody);
            }

            if (response.getStatus() != 201) {
                String errorBody = response.readEntity(String.class);
                log.error("Error no controlado en Keycloak. Status: {}, Body: {}",
                        response.getStatus(), errorBody);
                throw new RuntimeException("Error inesperado al comunicarse con Keycloak");
            }

            String location = response.getHeaderString("Location");
            String userId = location.substring(location.lastIndexOf("/") + 1);

            return UUID.fromString(userId);
        }
    }
}
