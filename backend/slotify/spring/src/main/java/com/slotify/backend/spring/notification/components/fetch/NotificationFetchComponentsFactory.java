package com.slotify.backend.spring.notification.components.fetch;

import com.slotify.backend.spring.auth.enums.AccountType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class NotificationFetchComponentsFactory {

    private Map<AccountType, NotificationFetchComponentsIf> componentsMap;
    NotificationFetchComponentsFactory(List<NotificationFetchComponentsIf> components) {
        this.componentsMap = components.stream()
                .collect(Collectors.toMap(
                        NotificationFetchComponentsIf::supports,
                        Function.identity(),
                        (a, b) -> {
                            throw new IllegalStateException("No pueden existir 2 implementaciones del mismo account type: " + a.supports());
                        }
                ));
    }

    public NotificationFetchComponentsIf forAccountType(AccountType accountType) {
        NotificationFetchComponentsIf component = componentsMap.get(accountType);

        if (component == null) {
            throw new IllegalArgumentException("No existe un componente para este account type: " + accountType);
        }

        return component;
    }
}
