package com.slotify.backend.spring.company.models;

import com.slotify.backend.spring.service.models.ServiceEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "companies")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "services")
public class CompanyEntity {

    @Id
    @Column(name = "user_id")
    @EqualsAndHashCode.Include
    private UUID userId;

    @Column(name = "default_max_concurrent_services", nullable = false)
    private Integer defaultMaxConcurrentServices;

    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(name = "email_address", nullable = false, length = 320)
    private String emailAddress;

    @Column(name = "physical_address", nullable = false, length = 300)
    private String physicalAddress;

    @Column(name = "s3_image_key", length = 200)
    private String s3ImageKey;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "ratting_avg")
    private Short rattingAvg;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    List<ServiceEntity> services;

}
