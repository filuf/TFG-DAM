package com.slotify.backend.spring.service.mappers;

import com.slotify.backend.spring.company.dtos.GetServicesResponse;
import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.service.dtos.CreateServiceResponse;
import com.slotify.backend.spring.service.dtos.ScheduleSummary;
import com.slotify.backend.spring.service.enums.ServiceFetchMode;
import com.slotify.backend.spring.service.models.ServiceEntity;
import com.slotify.backend.spring.service.models.ServiceScheduleEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ServiceMapper {
    public ServiceEntity toEntity(CompanyEntity company, String serviceName, Integer minutesDuration, Integer priceCent, String description) {
        return ServiceEntity.builder()
                .company(company)
                .serviceName(serviceName)
                .serviceMinutesDuration(minutesDuration)
                .servicePriceCent(priceCent)
                .description(description)
                .build();
    }

    public CreateServiceResponse toCreateServiceResponse(ServiceEntity serviceEntity) {
        return CreateServiceResponse.builder()
                .serviceId(serviceEntity.getServiceId())
                .serviceName(serviceEntity.getServiceName())
                .minutesDuration(serviceEntity.getServiceMinutesDuration())
                .priceCent(serviceEntity.getServicePriceCent())
                .description(serviceEntity.getDescription())
                .build();
    }

    public GetServicesResponse toGetServicesResponse(ServiceEntity serviceEntity, ServiceFetchMode fetchMode) {
        GetServicesResponse.GetServicesResponseBuilder builder = GetServicesResponse.builder()
                .serviceId(serviceEntity.getServiceId())
                .description(serviceEntity.getDescription())
                .s3ImageKey(serviceEntity.getS3ImageKey())
                .servicePriceCent(serviceEntity.getServicePriceCent())
                .serviceName(serviceEntity.getServiceName())
                .serviceMinutesDuration(serviceEntity.getServiceMinutesDuration());

        if (ServiceFetchMode.WITH_SCHEDULES.equals(fetchMode)) {
            builder.schedules(this.toScheduleSummaryList(serviceEntity.getSchedules()));
        }

        return builder.build();
    }

    private List<ScheduleSummary> toScheduleSummaryList(List<ServiceScheduleEntity> scheduleEntityList) {
        return scheduleEntityList.stream()
                .map(this::toScheduleSummary)
                .toList();
    }
    private ScheduleSummary toScheduleSummary(ServiceScheduleEntity scheduleEntity) {
        return ScheduleSummary.builder()
                .id(scheduleEntity.getId())
                .dayOfWeek(scheduleEntity.getDayOfWeek())
                .startTime(scheduleEntity.getStartTime())
                .endTime(scheduleEntity.getEndTime())
                .build();
    }
}
