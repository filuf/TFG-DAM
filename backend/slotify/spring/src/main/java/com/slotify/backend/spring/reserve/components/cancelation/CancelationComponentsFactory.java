package com.slotify.backend.spring.reserve.components.cancelation;

import com.slotify.backend.spring.auth.enums.AccountType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CancelationComponentsFactory {

    private final Map<AccountType, CancelationComponentsIf> componentsByType;

    CancelationComponentsFactory(List<CancelationComponentsIf> componentsList) {
        this.componentsByType = componentsList.stream()
                .collect(Collectors.toMap(
                        CancelationComponentsIf::supports,
                        Function.identity(),
                        (a, b) -> {
                            throw new IllegalStateException("No pueden existir 2 implementaciones del mismo account type: " + a.supports());
                        }
                ));
    }

    public CancelationComponentsIf forAccountType(AccountType accountType) {
        CancelationComponentsIf component = componentsByType.get(accountType);

        if (component == null) {
            throw new IllegalArgumentException("No existe un componente para este account type: " + accountType);
        }

        return component;
    }


}
