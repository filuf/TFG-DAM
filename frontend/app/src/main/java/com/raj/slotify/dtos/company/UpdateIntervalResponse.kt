package com.raj.slotify.dtos.company

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.util.UUID

data class UpdateIntervalResponse(

    @SerializedName("intervalId")
    val intervalId: UUID,

    @SerializedName("startDatetime")
    val startDateTime: LocalDateTime,

    @SerializedName("endDatetime")
    val endDateTime: LocalDateTime,

    @SerializedName("maxConcurrentServices")
    val maxConcurrentServices: Int,

    @SerializedName("createdAt")
    val createdAt: LocalDateTime

)
