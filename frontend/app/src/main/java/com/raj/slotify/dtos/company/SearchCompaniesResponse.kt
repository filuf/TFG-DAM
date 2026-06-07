package com.raj.slotify.dtos.company

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class SearchCompaniesResponse(
    @SerializedName("companyId")
    val companyId: UUID,

    @SerializedName("companyName")
    val companyName: String,

    @SerializedName("physicalAddress")
    val physicalAddress: String,

    @SerializedName("s3ImageKey")
    val s3ImageKey: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("rattingAvg")
    val rattingAvg: String?,
)
