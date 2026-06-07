package com.raj.slotify.dtos.user

import com.google.gson.annotations.SerializedName

data class PatchUserRequest(

    @SerializedName("username")
    val username: String,

    @SerializedName("phoneNumber")
    val phoneNumber: String,

)
