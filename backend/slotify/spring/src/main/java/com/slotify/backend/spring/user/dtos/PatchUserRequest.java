package com.slotify.backend.spring.user.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PatchUserRequest {

    private JsonNullable<String> username = JsonNullable.undefined();
    private JsonNullable<String> phoneNumber = JsonNullable.undefined();


    @Size(min = 5, max = 70)
    public String getUsername() {
        return username.orElse(null);
    }

    @JsonProperty("username")
    public JsonNullable<String> getUsernameJsonNullable() {
        return username;
    }

    @Size(min = 1, max = 15)
    @Pattern(regexp = "^[0-9]+$", message = "El número de teléfono debe contener solo dígitos")
    public String getPhoneNumber() {
        return phoneNumber.orElse(null);
    }

    @JsonProperty("phoneNumber")
    public JsonNullable<String> getPhoneNumberJsonNullable() {
        return phoneNumber;
    }
}
