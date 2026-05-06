package com.slotify.backend.spring.service.repositories;

import com.slotify.backend.spring.service.projections.ServiceScheduleDTO;
import com.slotify.backend.spring.service.models.ServiceEntity;
import com.slotify.backend.spring.service.projections.ServiceDurationRange;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceRepository extends JpaRepository<ServiceEntity, UUID> {

    Page<ServiceEntity> findByCompany_UserId(UUID userId, Pageable pageable);

    List<ServiceEntity> findByCompany_UserId(UUID userId);

    @Query("SELECT MIN(s.serviceMinutesDuration) as minDuration, " +
            "MAX(s.serviceMinutesDuration) as maxDuration " +
            "FROM ServiceEntity s WHERE s.company.userId = :companyId")
    ServiceDurationRange findDurationRangeByCompanyId(@Param("companyId") UUID userId);

    @Query("""
    SELECT DISTINCT s.serviceMinutesDuration
    FROM ServiceEntity s
    WHERE s.company.userId = :companyId
    ORDER BY s.serviceMinutesDuration ASC
    """)
    List<Integer> findDistinctDurationsByCompanyIdOrderAsc(@Param("companyId") UUID userId);

    @EntityGraph(attributePaths = {"company"})
    Optional<ServiceEntity> findWithCompanyByServiceId(UUID serviceId);

    @Query("""
    SELECT new com.slotify.backend.spring.service.projections.ServiceScheduleDTO(
        s.serviceId,
        s.serviceMinutesDuration,
        sch.dayOfWeek,
        sch.startTime,
        sch.endTime
    )
    FROM ServiceEntity s
    JOIN s.schedules sch
    WHERE s.company.userId = :companyId
    AND sch.dayOfWeek IN :days
    """)
    List<ServiceScheduleDTO> findServiceSchedulesDTOByCompanyIdAndDays(
            @Param("companyId") UUID companyId,
            @Param("days") List<Integer> days
    );
}
