package com.raj.slotify.dtos.service

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class CreateServiceScheduleResponse(

    @SerializedName("scheduleId")
    val scheduleId: UUID,

    @SerializedName("serviceName")
    val serviceName: String,

    @SerializedName("dayOfWeek")
    val dayOfWeek: Int,

    @SerializedName("interval")
    val interval: String

)
