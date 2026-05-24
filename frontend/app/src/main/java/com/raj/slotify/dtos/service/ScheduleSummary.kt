package com.raj.slotify.dtos.service

import com.google.gson.annotations.SerializedName
import java.time.LocalTime
import java.util.UUID

data class ScheduleSummary(

    @SerializedName("id")
    val id: UUID,

    @SerializedName("dayOfWeek")
    val dayOfWeek: Int,

    @SerializedName("startTime")
    val startTime: LocalTime,

    @SerializedName("endTime")
    val endTime: LocalTime

)
