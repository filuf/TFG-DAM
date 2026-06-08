package com.raj.slotify.dtos.service

import com.google.gson.annotations.SerializedName
import java.time.DayOfWeek

data class CreateServiceScheduleRequest(

    @SerializedName("dayOfWeek")
    val dayOfWeek: DayOfWeek,

    @SerializedName("startTime")
    val startTime: String,

    @SerializedName("endTime")
    val endTime: String

)
