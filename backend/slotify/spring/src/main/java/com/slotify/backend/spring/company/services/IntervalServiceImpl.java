package com.slotify.backend.spring.company.services;

import com.slotify.backend.spring.company.enums.IntervalFetchMode;
import com.slotify.backend.spring.company.models.CompanyIntervalEntity;
import com.slotify.backend.spring.company.repositories.IntervalRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
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
    public List<CompanyIntervalEntity> findIntervalsByCompanyId(UUID companyId, IntervalFetchMode fetchMode) {
        Specification<CompanyIntervalEntity> spec = (root, query, cb) -> {
            LocalDateTime now = LocalDateTime.now();
            Predicate companyPredicate = cb.equal(root.get("company").get("userId"), companyId);
            Predicate timePredicate = switch (fetchMode) {
                case PAST -> cb.lessThan(root.get("endDatetime"), now);
                case PRESENT -> cb.greaterThan(root.get("endDatetime"), now);
                case ALL -> cb.conjunction();
            };
            query.orderBy(cb.asc(root.get("endDatetime")));
            return cb.and(companyPredicate, timePredicate);
        };
        return this.intervalRepository.findAll(spec);
    }

    @Override
    public Optional<CompanyIntervalEntity> findIntervalById(UUID intervalId) {
        return this.intervalRepository.findById(intervalId);
    }

    @Override
    public void deleteInterval(UUID intervalId) {
        this.intervalRepository.deleteById(intervalId);
    }
}
