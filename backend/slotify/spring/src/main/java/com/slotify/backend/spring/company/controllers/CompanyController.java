package com.slotify.backend.spring.company.controllers;

import com.slotify.backend.spring.company.dtos.GetCompanyResponse;
import com.slotify.backend.spring.company.dtos.GetServicesResponse;
import com.slotify.backend.spring.company.dtos.PatchCompanyRequest;
import com.slotify.backend.spring.company.dtos.PatchCompanyResponse;
import com.slotify.backend.spring.company.dtos.SearchCompaniesResponse;
import com.slotify.backend.spring.company.enums.CompanyFetchMode;
import com.slotify.backend.spring.company.useCases.GetCompanyUseCase;
import com.slotify.backend.spring.company.useCases.GetServicesUseCase;
import com.slotify.backend.spring.company.useCases.PatchCompanyUseCase;
import com.slotify.backend.spring.company.useCases.SearchCompaniesUseCase;
import com.slotify.backend.spring.service.enums.ServiceFetchMode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/companies")
public class CompanyController {

    private static final Integer ITEMS_PER_PAGE = 10;
    private final GetServicesUseCase getServicesUseCase;
    private final SearchCompaniesUseCase searchCompaniesUseCase;
    private final GetCompanyUseCase getCompanyUseCase;
    private final PatchCompanyUseCase patchCompanyUseCase;

    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<PatchCompanyResponse> patchCompany(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart("request") @Valid PatchCompanyRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) throws IOException {
        PatchCompanyResponse response = this.patchCompanyUseCase.patchCompany(
                UUID.fromString(jwt.getSubject()),
                file,
                request.getDefaultMaxConcurrentServicesJsonNullable(),
                request.getCompanyNameJsonNullable(),
                request.getPhoneNumberJsonNullable(),
                request.getPhysicalAddressJsonNullable(),
                request.getDescriptionJsonNullable()
        );

        return ResponseEntity.ok(response);
    }


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

    @GetMapping("/search")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<SearchCompaniesResponse>> searchCompanies(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") Integer page
    ) {
        Pageable pageable = PageRequest.of(page, ITEMS_PER_PAGE);
        Page<SearchCompaniesResponse> response = this.searchCompaniesUseCase.search(q, pageable);
        return ResponseEntity.ok(response);
    }
}
