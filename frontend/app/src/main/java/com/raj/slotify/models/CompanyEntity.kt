package com.raj.slotify.models

import java.util.UUID

data class CompanyEntity (
    var userId: UUID,
    var defaultMaxConcurrentServices: Int,
    var companyName: String,
    var phoneNumber: String,
    var emailAddress: String,
    var physicalAddress: String?,
    var s3ImageKey: String?,
    var description: String,
    var ratingAvg: String
)