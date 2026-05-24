package com.raj.slotify.models

import java.time.LocalTime
import java.util.UUID

data class ServiceScheduleEntity (
    var id: UUID,
    var dayOfWeek: Int,
    var startTime: LocalTime,
    var endTime: LocalTime
)