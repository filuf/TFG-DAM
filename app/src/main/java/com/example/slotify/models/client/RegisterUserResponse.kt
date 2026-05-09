package com.example.slotify.models.client

import java.util.UUID

// DATA CLASS FOR USER REGISTRATION RESPONSE
data class RegisterUserResponse(
    val userId: UUID,
    val username: String,
    val email: String
)