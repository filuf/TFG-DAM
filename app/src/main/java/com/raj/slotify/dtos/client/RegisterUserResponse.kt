package com.raj.slotify.dtos.client

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class RegisterUserResponse(
    @SerializedName("userId")
    val userId: UUID,

    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String
)