package com.raj.slotify.dtos.service

import com.google.gson.annotations.SerializedName
import java.time.DayOfWeek
import java.time.LocalTime

data class CreateServiceScheduleRequest(

    @SerializedName("dayOfWeek")
    val dayOfWeek: DayOfWeek,

    @SerializedName("startTime")
    val startTime: LocalTime,

    @SerializedName("endTime")
    val endTime: LocalTime

)
