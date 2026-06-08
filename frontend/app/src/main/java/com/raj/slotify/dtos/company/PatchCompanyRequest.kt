package com.raj.slotify.dtos.company

import com.google.gson.annotations.SerializedName

data class PatchCompanyRequest(
    @SerializedName("defaultMaxConcurrentServices")
    val defaultMaxConcurrentServices: Int,

    @SerializedName("phoneNumber")
    val phoneNumber: String?,

    @SerializedName("physicalAddress")
    val physicalAddress: String?,

    @SerializedName("description")
    val description: String?,
)
