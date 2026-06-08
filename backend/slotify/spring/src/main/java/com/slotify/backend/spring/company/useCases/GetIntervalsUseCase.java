package com.slotify.backend.spring.company.useCases;

import com.slotify.backend.spring.company.dtos.IntervalSummary;
import com.slotify.backend.spring.company.enums.IntervalFetchMode;

import java.util.List;
import java.util.UUID;

public interface GetIntervalsUseCase {

    List<IntervalSummary> getIntervals(UUID companyId, IntervalFetchMode fetchMode);
}
