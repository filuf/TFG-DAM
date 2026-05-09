package com.raj.slotify.models

data class UserRegistryData(
    val userType: String,
    val defaultMaxCurrentServices: Int,
    val companyName: String,
    val phoneNumber: String,
    val emailAddress: String,
    val physicalAddress: String,
    val s3ImageKey: String,
    val description: String,
    val rattingAVG: Float,
    val userName: String,
    val email: String
)
