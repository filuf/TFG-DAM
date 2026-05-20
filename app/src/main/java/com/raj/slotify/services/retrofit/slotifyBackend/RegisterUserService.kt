package com.raj.slotify.services.retrofit.slotifyBackend

import com.raj.slotify.dtos.registry.client.RegisterUserRequest
import com.raj.slotify.dtos.registry.client.RegisterUserResponse
import com.raj.slotify.dtos.registry.company.RegisterCompanyRequest
import com.raj.slotify.dtos.registry.company.RegisterCompanyResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RegisterUserService {

    @POST("auth/register/user")
    suspend fun registerClient(
        @Body registerClientRequest: RegisterUserRequest
    ): Response<RegisterUserResponse>

    @POST("auth/register/company")
    suspend fun registerCompany(
        @Body registerCompanyRequest: RegisterCompanyRequest
    ): Response<RegisterCompanyResponse>

}