package com.raj.slotify.dtos.company

import com.google.gson.annotations.SerializedName
import java.time.LocalTime

data class CreateIntervalRequest(

    @SerializedName("startDateTime")
    val startDateTime: LocalTime,

    @SerializedName("endDateTime")
    val endDateTime: LocalTime,

    @SerializedName("maxConcurrentServices")
    val maxConcurrentServices: Int

)
