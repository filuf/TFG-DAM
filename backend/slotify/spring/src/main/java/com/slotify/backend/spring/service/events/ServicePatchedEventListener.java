package com.slotify.backend.spring.service.events;

import com.slotify.backend.spring.company.models.CompanyDocument;
import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.company.services.CompanySearchService;
import com.slotify.backend.spring.service.dtos.ServicePatchedEvent;
import com.slotify.backend.spring.service.models.ServiceDocument;
import com.slotify.backend.spring.service.models.ServiceEntity;
import com.slotify.backend.spring.service.services.ServiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServicePatchedEventListener {

    private final CompanySearchService companySearchService;
    private final ServiceService serviceService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void reindexCompany(ServicePatchedEvent event) {

        CompanyEntity companyEntity = event.getCompanyEntity();

        List<ServiceEntity> services = serviceService.findServicesByCompanyId(companyEntity.getUserId());

        List<ServiceDocument> serviceDocuments = services.stream()
                .map(service -> new ServiceDocument(
                        service.getServiceId().toString(),
                        service.getServiceName(),
                        service.getDescription())
                )
                .toList();

        CompanyDocument newDocument = CompanyDocument.builder()
                .companyId(companyEntity.getUserId().toString())
                .companyName(companyEntity.getCompanyName())
                .description(companyEntity.getDescription())
                .physicalAddress(companyEntity.getPhysicalAddress())
                .s3ImageKey(companyEntity.getS3ImageKey())
                .services(serviceDocuments)
                .build();

        this.companySearchService.indexCompany(newDocument);

    }

}
