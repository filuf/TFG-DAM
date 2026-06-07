package com.slotify.backend.spring.service.models;

import com.slotify.backend.spring.company.models.CompanyEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "services")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"schedules", "company"})
public class ServiceEntity {

    @Id
    @Column(name = "service_id")
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID serviceId;

    @Column(name = "service_name", nullable = false, length = 45)
    private String serviceName;

    @Column(name = "service_minutes_duration", nullable = false)
    private Integer serviceMinutesDuration;

    @Column(name = "service_price_cent", nullable = false)
    private Integer servicePriceCent;

    @Column(name = "s3_image_key", length = 200)
    private String s3ImageKey;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyEntity company;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dayOfWeek asc, startTime asc")
    @BatchSize(size = 30)
    private List<ServiceScheduleEntity> schedules = new ArrayList<>();
}
