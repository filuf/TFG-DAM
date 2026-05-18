package com.slotify.backend.spring.company.controllers;

import com.slotify.backend.spring.company.dtos.GetCompanyResponse;
import com.slotify.backend.spring.company.dtos.GetServicesResponse;
import com.slotify.backend.spring.company.enums.CompanyFetchMode;
import com.slotify.backend.spring.company.useCases.GetCompanyUseCase;
import com.slotify.backend.spring.company.useCases.GetServicesUseCase;
import com.slotify.backend.spring.service.enums.ServiceFetchMode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/companies")
public class CompanyController {

    private static final Integer ITEMS_PER_PAGE = 10;
    private final GetServicesUseCase getServicesUseCase;
    private final GetCompanyUseCase getCompanyUseCase;


    @GetMapping("/{companyId}")
    public ResponseEntity<GetCompanyResponse>getCompanyByCompanyId(
            @PathVariable UUID companyId,
            @RequestParam(defaultValue = "BASIC") CompanyFetchMode fetchMode

    ) {
        GetCompanyResponse response = this.getCompanyUseCase.getCompany(companyId, fetchMode);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{companyId}/services")
    public ResponseEntity<Page<GetServicesResponse>>getServicesByCompanyId(
            @PathVariable UUID companyId,
            @RequestParam(defaultValue = "BASIC") ServiceFetchMode fetchMode,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String order
    ) {
        Sort.Direction direction = order.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Map<String, String> whitelistSortMap = Map.of(
                "name", "serviceName",
                "price", "servicePriceCent"
        );
        String sortField = whitelistSortMap.getOrDefault(sortBy, "serviceName");

        Sort sort = Sort.by(direction, sortField);
        Pageable pageable = PageRequest.of(page, ITEMS_PER_PAGE, sort);

        Page<GetServicesResponse> response = this.getServicesUseCase.getServices(companyId, fetchMode, pageable);
        return ResponseEntity.ok(response);
    }
}
