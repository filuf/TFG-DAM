package com.slotify.backend.spring.company.services;

import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.company.models.CompanyIntervalEntity;
import com.slotify.backend.spring.company.repositories.IntervalRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IntervalServiceImpl implements IntervalService {

    private final IntervalRepository intervalRepository;

    @Override
    public CompanyIntervalEntity saveInterval(CompanyIntervalEntity intervalEntity) {
        return this.intervalRepository.save(intervalEntity);
    }

    @Override
    public List<CompanyIntervalEntity> findIntervalsByCompanyId(UUID companyId) {
        return this.intervalRepository.findByCompany_UserId(companyId);
    }

    @Override
    public Optional<CompanyIntervalEntity> findIntervalById(UUID intervalId) {
        return this.intervalRepository.findById(intervalId);
    public Optional<CompanyIntervalEntity> findIntervalByCompanyIdAndStartDateTime(UUID companyId, LocalDateTime dateTime) {
        return intervalRepository.findOne( (root, query, criteriaBuilder) -> {

            Join<CompanyIntervalEntity, CompanyEntity> companyJoin = root.join("company");
            Predicate userPred = criteriaBuilder.equal(companyJoin.get("userId"), companyId);

            Predicate startDatetimePred = criteriaBuilder.lessThanOrEqualTo(root.get("startDatetime"), dateTime);
            Predicate endDatetimePred = criteriaBuilder.greaterThanOrEqualTo(root.get("endDatetime"), dateTime);

            return criteriaBuilder.and(userPred, startDatetimePred, endDatetimePred);
        });
    }

    @Override
    public List<CompanyIntervalEntity> findIntervalByCompanyIdAndBetweenDatesTime(UUID companyId, LocalDateTime reserveStartDateTime, LocalDateTime reserveEndDateTime) {
        return intervalRepository.findAll( (root, query, criteriaBuilder) -> {

            Join<CompanyIntervalEntity, CompanyEntity> companyJoin = root.join("company");
            Predicate userPred = criteriaBuilder.equal(companyJoin.get("userId"), companyId);

            Predicate startDatetimePred = criteriaBuilder.lessThan(root.get("startDatetime"), reserveEndDateTime);
            Predicate endDatetimePred = criteriaBuilder.greaterThan(root.get("endDatetime"), reserveStartDateTime);

            return criteriaBuilder.and(userPred, startDatetimePred, endDatetimePred);
        });
    }
}
