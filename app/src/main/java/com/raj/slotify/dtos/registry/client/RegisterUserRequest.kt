package com.raj.slotify.dtos.registry.client

import com.google.gson.annotations.SerializedName

data class RegisterUserRequest(

    @SerializedName("username")
    val userName: String? = null,

    @SerializedName("password")
    val password: String? = null,

    @SerializedName("emailAddress")
    val emailAddress: String? = null

)