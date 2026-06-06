package com.raj.slotify.models.company

import java.util.UUID

data class CompanyData (

    var companyId: UUID,
    var defaultMaxCurrentServices: Int,
    var companyName: String,
    var phoneNumber: Int,
    var emailAddress: String,
    var physicalAddress: String,
    var s3ImageKey: String,
    var description: String,
    var rattingAVG: Float

)