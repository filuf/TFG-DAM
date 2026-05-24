package com.raj.slotify.dtos.reserves

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.util.UUID

data class CreateReserveResponse(

    @SerializedName("reserveId")
    val reserveId: UUID,

    @SerializedName("reserveDateTime")
    val reserveDateTime: LocalDateTime,

    @SerializedName("companyName")
    val companyName: String,

    @SerializedName("serviceName")
    val serviceName: String

)
