package com.slotify.backend.spring.service.mappers;

import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.service.dtos.CreateServiceResponse;
import com.slotify.backend.spring.service.models.ServiceEntity;
import org.springframework.stereotype.Component;

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
}
