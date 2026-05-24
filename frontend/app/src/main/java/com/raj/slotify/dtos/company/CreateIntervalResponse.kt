package com.raj.slotify.dtos.company

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class CreateIntervalResponse(

    @SerializedName("intervalId")
    val intervalId: UUID,

    @SerializedName("startDateTime")
    val startDateTime: String,

    @SerializedName("endDateTime")
    val endDateTime: String,

    @SerializedName("maxConcurrentServices")
    val maxConcurrentServices: Int

)
