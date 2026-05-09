package com.example.slotify.services.retrofit

import com.example.slotify.dtos.maps.PlaceSuggestion
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface GoogleMapsApiService {

    @GET("search")
    suspend fun getMap(
        @Query("q") place: String,
        @Query("format") format: String,
        @Header("User-Agent") userAgent: String
    ): Response<List<PlaceSuggestion>>

}