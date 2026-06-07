package com.slotify.backend.spring.service.events;


import com.slotify.backend.spring.company.models.CompanyDocument;
import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.company.services.CompanySearchService;
import com.slotify.backend.spring.service.dtos.ServiceCreatedEvent;
import com.slotify.backend.spring.service.models.ServiceDocument;
import com.slotify.backend.spring.service.models.ServiceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceCreatedEventListener {

    private final CompanySearchService companySearchService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void indexNewEvent(ServiceCreatedEvent event) {

        CompanyEntity companyEntity = event.getCompanyEntity();
        ServiceEntity serviceEntity = event.getServiceEntity();

        CompanyDocument companyDocument = this.companySearchService.findCompanyById(companyEntity.getUserId().toString());

        if (companyDocument == null) {
            log.error("No se ha encontrado la compañía en ElasticSearch companyId: {}, No se ha podido indexar el servivio serviceId: {}",
                    companyEntity.getUserId(), serviceEntity.getServiceId());
            return;
        }

        ServiceDocument newService = ServiceDocument.builder()
                .serviceId(serviceEntity.getServiceId().toString())
                .serviceName(serviceEntity.getServiceName())
                .description(serviceEntity.getDescription())
                .build();


        List<ServiceDocument> original = companyDocument.getServices();
        if (original == null) {
            original = List.of();
        }

        ArrayList<ServiceDocument> serviceList = new ArrayList<>(original.size() + 1);
        serviceList.addAll(original);
        serviceList.add(newService);

        CompanyDocument newDocument = CompanyDocument.builder()
                .companyId(companyDocument.getCompanyId())
                .companyName(companyDocument.getCompanyName())
                .description(companyDocument.getDescription())
                .physicalAddress(companyDocument.getPhysicalAddress())
                .s3ImageKey(companyDocument.getS3ImageKey())
                .services(serviceList)
                .build();

        this.companySearchService.indexCompany(newDocument);
    }
}
