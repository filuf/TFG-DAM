package com.raj.slotify.services

import com.raj.slotify.MainActivity
import com.raj.slotify.models.UserRegistryData

class UserAccountManagement {

    fun createUserAccount(userData: UserRegistryData) {

        val endpoint: String = MainActivity.SPRING_ENDPOINT + "/auth/register"

        if (userData.userType == "client") {
            // CLIENTE

            val clientRegistryEndpoint: String = "$endpoint/client"

            val userName: String = userData.userName
            val s3ImageKey:String = userData.s3ImageKey
            val phoneNumber: String = userData.phoneNumber
            val email: String = userData.email



        }

        else if (userData.userType == "enterprise") {
            // EMPRESA

            val enterpriseRegistryEndpoint: String = "$endpoint/company"

            val defaultMaxCurrentServices: Int = userData.defaultMaxCurrentServices
            val companyName: String = userData.companyName
            val phoneNumber: String = userData.phoneNumber
            val emailAddress: String = userData.emailAddress
            val physicalAddress: String = userData.physicalAddress
            val s3ImageKey: String = userData.s3ImageKey
            val description: String = userData.description
            val rattingAVG: Float = userData.rattingAVG
        }

    }

}