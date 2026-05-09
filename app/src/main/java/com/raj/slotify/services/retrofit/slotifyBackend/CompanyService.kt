package com.raj.slotify.services.retrofit.slotifyBackend

import com.raj.slotify.dtos.maps.PlaceSuggestion
import retrofit2.Response
import retrofit2.http.*
import java.util.UUID

interface CompanyService {

    @GET("{companyId}/services")
    suspend fun getServicesByCompanyId(
        @Path("companyId") companyId: UUID,
        @Query("fetchMode") place: String,
        @Query("page") page: Integer,
        @Query("sortBy") sortBy: String,
        @Query("order") order: String
    ): Response<List<PlaceSuggestion>>

}