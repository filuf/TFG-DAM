package com.raj.slotify.dtos.company

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class UpdateIntervalRequest(

    @SerializedName("startDateTime")
    val startDateTime: LocalDateTime,

    @SerializedName("endDateTime")
    val endDateTime: LocalDateTime,

    @SerializedName("maxConcurrentService")
    val maxConcurrentService: Int

)
