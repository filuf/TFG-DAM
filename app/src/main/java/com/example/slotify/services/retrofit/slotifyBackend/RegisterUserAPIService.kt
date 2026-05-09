package com.example.slotify.services.retrofit.slotifyBackend

import com.example.slotify.fragments.mapsFragment.PlaceSuggestion
import com.example.slotify.models.client.RegisterUserRequest
import com.example.slotify.models.client.RegisterUserResponse
import com.example.slotify.models.company.RegisterCompanyRequest
import com.example.slotify.models.company.RegisterCompanyResponse
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