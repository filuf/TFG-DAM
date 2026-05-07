package com.slotify.backend.spring.reserve.components.fetch;

import com.slotify.backend.spring.reserve.dtos.ReserveSummary;
import com.slotify.backend.spring.reserve.enums.ReserveFetchType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ReserveFetcher<T extends ReserveSummary> {


    Page<T> fetch(UUID accountId, ReserveFetchType reserveFetchType, Pageable pageable);
}
