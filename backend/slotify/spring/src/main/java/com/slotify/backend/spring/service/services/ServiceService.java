package com.slotify.backend.spring.service.services;

import com.slotify.backend.spring.service.models.ServiceEntity;

public interface ServiceService {
    ServiceEntity saveService(ServiceEntity serviceEntity);
}
