package com.slotify.backend.spring.company.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
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
public class PatchCompanyRequest {

    private JsonNullable<Integer> defaultMaxConcurrentServices = JsonNullable.undefined();
    private JsonNullable<String> phoneNumber = JsonNullable.undefined();
    private JsonNullable<String> physicalAddress = JsonNullable.undefined();
    private JsonNullable<String> description = JsonNullable.undefined();

    @Min(1)
    public Integer getDefaultMaxConcurrentServices() {
        return defaultMaxConcurrentServices.orElse(null);
    }

    @JsonProperty("defaultMaxConcurrentServices")
    public JsonNullable<Integer> getDefaultMaxConcurrentServicesJsonNullable() {
        return defaultMaxConcurrentServices;
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

    @Size(min = 1, max = 300)
    public String getPhysicalAddress() {
        return physicalAddress.orElse(null);
    }

    @JsonProperty("physicalAddress")
    public JsonNullable<String> getPhysicalAddressJsonNullable() {
        return physicalAddress;
    }

    public String getDescription() {
        return description.orElse(null);
    }

    @JsonProperty("description")
    public JsonNullable<String> getDescriptionJsonNullable() {
        return description;
    }
}
