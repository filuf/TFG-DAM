package com.example.slotify.dtos.company

data class RegisterCompanyRequest(
    var companyName: String? = null,
    var password: String? = null,
    var emailAddress: String? = null,
    var defaultMaxConcurrentServices: Int? = null,
    var physicalAddress: String? = null
)