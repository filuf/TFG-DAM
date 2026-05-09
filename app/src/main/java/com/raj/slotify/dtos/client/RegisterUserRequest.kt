package com.example.slotify.dtos.client

// COMPACT DATA CLASS FOR USER REGISTRATION REQUEST WITHOUT VALIDATIONS
data class RegisterUserRequest(
    var username: String? = null,
    var password: String? = null,
    var emailAddress: String? = null
)