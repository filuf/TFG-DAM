package com.example.slotify.models.company

import java.util.UUID

// IMMUTABLE DATA CLASS FOR COMPANY REGISTRATION RESPONSE
data class RegisterCompanyResponse(
    val userId: UUID,
    val username: String,
    val email: String,
    val physicalAddress: String
)