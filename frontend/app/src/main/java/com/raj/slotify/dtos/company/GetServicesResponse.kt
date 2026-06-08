package com.raj.slotify.dtos.company

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class GetServicesResponse(

    @SerializedName("serviceId")
    val serviceId: UUID,

    @SerializedName("serviceName")
    val serviceName: String,

    @SerializedName("serviceMinutesDuration")
    val serviceMinutesDuration: Int,

    @SerializedName("servicePriceCent")
    val servicePriceCent: Int,

    @SerializedName("s3ImageUrl")
    val s3ImageUrl: String?,

    @SerializedName("description")
    val description: String?

)
