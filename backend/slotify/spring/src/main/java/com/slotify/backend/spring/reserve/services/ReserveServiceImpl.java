package com.slotify.backend.spring.reserve.services;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.reserve.enums.ReserveFetchType;
import com.slotify.backend.spring.reserve.models.ReserveEntity;
import com.slotify.backend.spring.reserve.repositories.ReserveRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReserveServiceImpl implements ReserveService {

    private final ReserveRepository reserveRepository;

    @Override
    public ReserveEntity saveReserve(ReserveEntity reserve) {
        return this.reserveRepository.save(reserve);
    }

    @Override
    public Optional<ReserveEntity> findReserveById(UUID reserveId) {
        return this.reserveRepository.findById(reserveId);
    }

    @Override
    public List<ReserveEntity> findReservesByUserId(UUID userId) {
        return this.reserveRepository.findByUser_UserId(userId);
    }

    @Override
    public List<ReserveEntity> findAllReservesByUserIdBetweenDateTimes(UUID userId, LocalDateTime start, LocalDateTime end) {
        return this.reserveRepository.findByUser_UserIdAndServiceTimeBetweenAndIsCanceledFalse(userId, start, end);
    }

    @Override
    public List<ReserveEntity> findAllReservesByCompanyIdBetweenDateTimes(UUID companyId, LocalDateTime start, LocalDateTime end) {
        return this.reserveRepository.findByService_Company_UserIdAndServiceTimeBetweenAndIsCanceledFalse(companyId, start, end);
    }

    @Override
    public Page<ReserveEntity> findAllReservesByAccountIdAndAccountTypeAndFetchType(
            UUID accountId,
            AccountType accountType,
            ReserveFetchType reserveFetchType,
            Pageable pageable
    ) {

        Specification<ReserveEntity> spec = ((root, query, cb) -> {

            //specification ejecuta una segunda query en paginacion
            boolean isCountQuery = query.getResultType() == Long.class || query.getResultType() == long.class;

            Predicate accountPredicate = switch (accountType) {
                case USER -> {
                    if (!isCountQuery) {
                        root.fetch("service").fetch("company");
                    }
                    yield cb.equal(root.get("user").get("userId"), accountId);
                }
                case COMPANY -> {
                    if (!isCountQuery) {
                        root.fetch("user");
                        root.fetch("service");
                    }
                    yield cb.equal(root.get("service").get("company").get("userId"), accountId);
                }
            };

            LocalDateTime now = LocalDateTime.now();

            Predicate fetchPredicate = switch (reserveFetchType) {
                case ALL -> cb.conjunction();
                case PAST -> cb.lessThan(root.get("serviceTime"), now);
                case PRESENT -> cb.greaterThan(root.get("serviceTime"), now);
            };

            return cb.and(accountPredicate, fetchPredicate);
        });

        return this.reserveRepository.findAll(spec, pageable);
    }
}
