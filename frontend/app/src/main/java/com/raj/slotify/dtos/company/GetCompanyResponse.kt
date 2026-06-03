package com.raj.slotify.dtos.company

import com.google.gson.annotations.SerializedName
import com.raj.slotify.dtos.service.ServiceSummary
import java.util.UUID

data class GetCompanyResponse(
    @SerializedName("companyId")
    val companyId: UUID,

    @SerializedName("defaultMaxConcurrentServices")
    val defaultMaxConcurrentServices: Int,

    @SerializedName("companyName")
    val companyName: String,

    @SerializedName("phoneNumber")
    val phoneNumber: String,

    @SerializedName("emailAddress")
    val emailAddress: String,

    @SerializedName("physicalAddress")
    val physicalAddress: String,

    @SerializedName("s3ImageKey")
    val s3ImageKey: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("rattingAvg")
    val  rattingAvg: String,

    @SerializedName("services")
    val services: List<ServiceSummary>
)
