package com.example.slotify.services.retrofit.slotifyBackend

import com.example.slotify.dtos.client.RegisterUserRequest
import com.example.slotify.dtos.client.RegisterUserResponse
import com.example.slotify.dtos.company.RegisterCompanyRequest
import com.example.slotify.dtos.company.RegisterCompanyResponse
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