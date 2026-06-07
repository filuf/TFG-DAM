package com.slotify.backend.spring.company.events;

import com.slotify.backend.spring.company.dtos.PatchCompanyEvent;
import com.slotify.backend.spring.company.models.CompanyDocument;
import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.company.services.CompanySearchService;
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
public class CompanyEventListener {

    private final ServiceService serviceService;
    private final CompanySearchService companySearchService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void updateElasticIndex(PatchCompanyEvent event) {
        CompanyEntity companyEntity = event.getCompanyEntity();

        List<ServiceEntity> services = this.serviceService.findServicesByCompanyId(companyEntity.getUserId());

        CompanyDocument companyDocument = this.companySearchService.findCompanyById(companyEntity.getUserId().toString());

        if (companyDocument == null) {
            log.error("No se ha encontrado la compañía en ElasticSearch companyId: {}",
                    companyEntity.getUserId());
            return;
        }

        if (services == null) {
            services = List.of();
        }

        List<ServiceDocument> serviceDocuments = services.stream().map(serviceEntity -> ServiceDocument.builder()
                        .serviceId(serviceEntity.getServiceId().toString())
                        .serviceName(serviceEntity.getServiceName())
                        .description(serviceEntity.getDescription())
                        .build())
                .toList();

        CompanyDocument newDocument = CompanyDocument.builder()
                .companyId(companyEntity.getUserId().toString())
                .companyName(companyEntity.getCompanyName())
                .description(companyEntity.getDescription())
                .physicalAddress(companyEntity.getPhysicalAddress())
                .services(serviceDocuments)
                .build();

        this.companySearchService.indexCompany(newDocument);

    }

}
