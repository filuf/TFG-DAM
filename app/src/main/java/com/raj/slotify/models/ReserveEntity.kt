package com.raj.slotify.models

import java.time.LocalDateTime
import java.util.UUID

data class ReserveEntity(
    var id: UUID,
    var user: UserEntity,
    var service: ServiceEntity,
    var serviceTime: LocalDateTime,
    var createdAt: LocalDateTime?,
    var isCancelled: Boolean?
)
