package com.slotify.backend.spring.company.components;

import com.slotify.backend.spring.company.models.CompanyEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class CompanyOwnershipValidator {

    public void verify(UUID companyId, CompanyEntity companyEntity) {
        if (!companyEntity.getUserId().equals(companyId)) {
            log.warn("Un usuario de otra empresa intenta crear un intervalo de forma ilegal, usuarioMalvado: {}, empresaAtacada: {}",
                    companyId, companyEntity.getCompanyName());
            throw new AccessDeniedException("No posees los permisos necesarios para modificar este recurso");
        }
    }
}
