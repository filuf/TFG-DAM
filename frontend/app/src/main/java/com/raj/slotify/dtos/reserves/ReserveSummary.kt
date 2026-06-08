package com.raj.slotify.dtos.reserves

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.util.UUID

@Parcelize
sealed class ReserveSummary : Parcelable {
    abstract val reserveId: UUID
    abstract val serviceId: UUID
    abstract val startDateTime: LocalDateTime
    abstract val endDateTime: LocalDateTime
    abstract val minutesDuration: Int
    abstract val serviceName: String
    abstract val servicePriceCent: Int
    abstract val isCanceled: Boolean
}