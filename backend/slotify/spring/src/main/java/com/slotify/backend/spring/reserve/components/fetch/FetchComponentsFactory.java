package com.slotify.backend.spring.reserve.components.fetch;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.reserve.dtos.ReserveSummary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class FetchComponentsFactory {

    private final Map<AccountType, FetchComponentsIf<? extends ReserveSummary>> componentsByType;

    FetchComponentsFactory(List<FetchComponentsIf<? extends ReserveSummary>> componentsList) {
        this.componentsByType = componentsList.stream()
                .collect(Collectors.toMap(
                        FetchComponentsIf::supports,
                        Function.identity(),
                        (a, b) -> {
                            throw new IllegalStateException("No pueden existir 2 implementaciones del mismo account type: " + a.supports());
                        }
                ));
    }

    public FetchComponentsIf<? extends ReserveSummary> forAccountType(AccountType accountType) {
        FetchComponentsIf<? extends ReserveSummary> component = componentsByType.get(accountType);

        if (component == null) {
            throw new IllegalArgumentException("No existe un componente para este account type: " + accountType);
        }

        return component;
    }


}
