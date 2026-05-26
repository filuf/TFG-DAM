package com.raj.slotify.models

import java.util.UUID

data class ServiceEntity (
    var serviceID: UUID,
    var serviceName: String,
    var servicePriceCent: Integer,
    var s3ImageKey: String?,
    var description: String?,
    var company: CompanyEntity,
    var schedules: List<ServiceScheduleEntity>,
)