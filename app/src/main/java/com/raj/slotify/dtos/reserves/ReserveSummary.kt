package com.raj.slotify.dtos.reserves

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.util.UUID

data class ReserveSummary(

    @SerializedName("reserveId")
    val reserveId: UUID,

    @SerializedName("serviceId")
    val serviceId: UUID,

    @SerializedName("startDateTime")
    val startDateTime: LocalDateTime,

    @SerializedName("endDateTime")
    val endDateTime: LocalDateTime,

    @SerializedName("minutesDuration")
    val minutesDuration: Int,

    @SerializedName("serviceName")
    val serviceName: String,

    @SerializedName("servicePriceCent")
    val servicePriceCent: Int,

    @SerializedName("isCanceled")
    val isCanceled: Boolean

)