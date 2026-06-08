package com.raj.slotify.dtos.registry.company

import com.google.gson.annotations.SerializedName

data class RegisterCompanyRequest(

    @SerializedName("companyName")
    val companyName: String? = null,

    @SerializedName("password")
    val password: String? = null,

    @SerializedName("emailAddress")
    val emailAddress: String? = null,

    @SerializedName("defaultMaxConcurrentServices")
    val defaultMaxConcurrentServices: Int? = null,

    @SerializedName("physicalAddress")
    val physicalAddress: String? = null

)