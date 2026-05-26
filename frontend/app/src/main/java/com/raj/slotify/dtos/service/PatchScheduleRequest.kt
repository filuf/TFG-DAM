package com.raj.slotify.dtos.service

import com.google.gson.annotations.SerializedName

data class PatchScheduleRequest(

    @SerializedName("dayOfWeek")
    val dayOfWeek: Int? = null,

    @SerializedName("startTime")
    val startTime: String? = null,

    @SerializedName("endTime")
    val endTime: String? = null

)
