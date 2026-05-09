package com.raj.slotify.dtos.registry.company

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class RegisterCompanyResponse(

    @SerializedName("userId")
    val userId: UUID,

    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("physicalAddress")
    val physicalAddress: String

)