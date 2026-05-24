package com.raj.slotify.dtos.service

import com.google.gson.annotations.SerializedName

data class CreateServiceRequest(

    @SerializedName("serviceName")
    val serviceName: String,

    @SerializedName("minutesDuration")
    val minutesDuration: Int,

    @SerializedName("priceCent")
    val priceCent: Int,

    @SerializedName("description")
    val description: String

)
