package com.slotify.backend.spring.service.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class PatchServiceRequest {

    private JsonNullable<String> serviceName = JsonNullable.undefined();
    private JsonNullable<Integer> minutesDuration = JsonNullable.undefined();
    private JsonNullable<Integer> priceCent = JsonNullable.undefined();
    private JsonNullable<String> description = JsonNullable.undefined();

    @Size(max = 45)
    public String getServiceName() {
        return serviceName.orElse(null);
    }

    @JsonProperty("serviceName")
    public JsonNullable<String> getServiceNameJsonNullable() {
        return serviceName;
    }

    @Min(5)
    @Max(480)
    public Integer getMinutesDuration() {
        return minutesDuration.orElse(null);
    }

    @JsonProperty("minutesDuration")
    public JsonNullable<Integer> getMinutesDurationJsonNullable() {
        return minutesDuration;
    }

    @Min(100)
    @Max(1000000)
    public Integer getPriceCent() {
        return priceCent.orElse(null);
    }

    @JsonProperty("priceCent")
    public JsonNullable<Integer> getPriceCentJsonNullable() {
        return priceCent;
    }

    @JsonProperty("description")
    public JsonNullable<String> getDescriptionJsonNullable() {
        return description;
    }
}
