package com.slotify.backend.spring.company.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "companies")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class CompanyEntity {

    @Id
    @Column(name = "user_id")
    @EqualsAndHashCode.Include
    private UUID userId;

    @Column(name = "default_max_concurrent_services", nullable = false)
    private Integer defaultMaxConcurrentServices;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email_address", nullable = false)
    private String emailAddress;

    @Column(name = "physical_address", nullable = false)
    private String physicalAddress;

    @Column(name = "s3_image_key")
    private String s3ImageKey;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "ratting_avg")
    private Short rattingAvg;

}
