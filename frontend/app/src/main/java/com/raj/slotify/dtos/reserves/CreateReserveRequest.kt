package com.raj.slotify.dtos.reserves

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.util.UUID

data class CreateReserveRequest(

    @SerializedName("dateTimeReserve")
    val dateTimeReserve: LocalDateTime,

    @SerializedName("serviceId")
    val serviceId: UUID

)
