package com.slotify.backend.spring.reserve.dtos;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@SuperBuilder
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "summaryType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = UserReserveSummary.class, name = "USER"),
        @JsonSubTypes.Type(value = CompanyReserveSummary.class, name = "COMPANY")
})
public abstract class ReserveSummary {
    private UUID reserveId;
    private UUID serviceId;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer minutesDuration;

    private String serviceName;
    private Integer servicePriceCent;

    private Boolean isCanceled;
}
