package com.example.slotify.models.company

data class CompanyData (
    val defaultMaxCurrentServices: Int,
    val companyName: String,
    val phoneNumber: Int,
    val emailAddress: String,
    val physicalAddress: String,
    val s3ImageKey: String,
    val description: String,
    val rattingAVG: Float
)