package com.raj.slotify.dtos.user

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.util.UUID

data class UserSummary(
    @SerializedName("userId")
    val userId: UUID,

    @SerializedName("username")
    val username: String,

    @SerializedName("s3ImageUrl")
    val s3ImageUrl: String?,

    @SerializedName("phoneNumber")
    val phoneNumber: String,

    @SerializedName("emailAddress")
    val emailAddress: String,

    @SerializedName("createdAt")
    val createdAt: LocalDateTime
)
