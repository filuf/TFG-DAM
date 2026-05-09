package com.example.slotify.services.retrofit.slotifyBackend

import com.example.slotify.fragments.mapsFragment.PlaceSuggestion
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface CompanyAPIService {

    @GET("{companyId}/services/")
    suspend fun getServicesByCompanyId(
        @Query("fetchMode") place: String,
        @Query("page") format: String,
        @Query("sortBy") sortBy: String,
        @Query("order") order: String
    ): Response<List<PlaceSuggestion>>

}