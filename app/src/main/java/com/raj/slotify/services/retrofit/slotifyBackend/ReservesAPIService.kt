package com.raj.slotify.services.retrofit.slotifyBackend

import com.raj.slotify.dtos.reserves.ReserveSummary
import com.raj.slotify.models.api.PageResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ReservesAPIService {

    @GET()
    suspend fun getReserves(
        @Header("Authorization") authHeader: String,

        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String?,

        @Query("fetchType") fetchType: String = "PRESENT"
    ): Response<PageResponse<ReserveSummary>>

    @POST
    suspend fun doAReserve(
        @Header("Authorization") authHeader: String,
        @Body createReserveRequest: CreateReserveRequest
    )

}