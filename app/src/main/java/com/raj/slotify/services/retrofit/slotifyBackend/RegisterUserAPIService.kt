package com.raj.slotify.services.retrofit.slotifyBackend

import com.raj.slotify.dtos.client.RegisterUserRequest
import com.raj.slotify.dtos.client.RegisterUserResponse
import com.raj.slotify.dtos.company.RegisterCompanyRequest
import com.raj.slotify.dtos.company.RegisterCompanyResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RegisterUserAPIService {

    @POST("register/user/")
    suspend fun registerClient(
        @Body registerClientRequest: RegisterUserRequest
    ): Response<RegisterUserResponse>

    @POST("register/company/")
    suspend fun registerCompany(
        @Body registerCompanyRequest: RegisterCompanyRequest
    ): Response<RegisterCompanyResponse>

}