package com.raj.slotify.dtos.company

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.util.UUID

data class IntervalSummary(

    @SerializedName("intervalId")
    val intervalId: UUID,

    @SerializedName("startDateTime")
    val startDateTime: LocalDateTime,

    @SerializedName("endDateTime")
    val endDateTime: LocalDateTime,

    @SerializedName("createdAt")
    val createdAt: LocalDateTime,

    @SerializedName("maxConcurrentServices")
    val maxConcurrentServices: Int

)
