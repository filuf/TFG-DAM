package com.raj.slotify.dtos.company

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class PatchCompanyResponse(
    @SerializedName("companyId")
    val companyId: UUID,

    @SerializedName("defaultMaxConcurrentServices")
    val defaultMaxConcurrentServices: Int,

    @SerializedName("companyName")
    val companyName: String,

    @SerializedName("phoneNumber")
    val phoneNumber: String,

    @SerializedName("physicalAddress")
    val physicalAddress: String,

    @SerializedName("s3ImageUrl")
    val s3ImageUrl: String?,

    @SerializedName("description")
    val description: String?
)
