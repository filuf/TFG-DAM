package com.slotify.backend.spring.company.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "max_concurrent_services_interval")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class CompanyIntervalEntity {

    @Id
    @Column(name = "interval_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID intervalId;

    @Column(name = "start_datetime", nullable = false)
    private LocalDateTime startDatetime;

    @Column(name = "end_datetime", nullable = false)
    private LocalDateTime endDatetime;

    @Column(name = "max_concurrent_services", nullable = false)
    private Integer maxConcurrentServices;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private CompanyEntity company;
}
