package com.raj.slotify.dtos.reserves

import com.google.gson.annotations.SerializedName
import java.time.LocalTime

data class TimeIntervalDTO (
    @SerializedName("startTime")
    val startTime: LocalTime,

    @SerializedName("endTime")
    val endTime: LocalTime
)