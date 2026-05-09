package com.example.slotify.models.company

// DATA CLASS WITH NULLABLE PROPERTIES TO SIMULATE NO ARGS CONSTRUCTOR
data class RegisterCompanyRequest(
    var companyName: String? = null,
    var password: String? = null,
    var emailAddress: String? = null,
    var defaultMaxConcurrentServices: Int? = null,
    var physicalAddress: String? = null
)