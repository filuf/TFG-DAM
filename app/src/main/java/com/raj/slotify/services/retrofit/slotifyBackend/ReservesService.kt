package com.raj.slotify.services.retrofit.slotifyBackend

import com.raj.slotify.dtos.reserves.CreateReserveRequest
import com.raj.slotify.dtos.reserves.CreateReserveResponse
import com.raj.slotify.dtos.reserves.ReserveSummary
import com.raj.slotify.models.api.PageResponse
import retrofit2.Response
import retrofit2.http.*
import java.util.UUID

interface ReservesService {

    @GET(".")
    suspend fun getReserves(
        @Header("Authorization") authHeader: String,

        @Query("page") page: Integer,
        @Query("size") size: Integer,
        @Query("sort") sort: String?,

        @Query("fetchType") fetchType: String = "PRESENT"
    ): Response<PageResponse<ReserveSummary>>

    @POST(".")
    suspend fun createReserve(
        @Header("Authorization") authHeader: String,
        @Body createReserveRequest: CreateReserveRequest
    ): Response<CreateReserveResponse>

    @POST("{reserveId}/cancel")
    suspend fun cancelReserve(
        @Path("reserveId") reserveId: UUID,
        @Header("Authorization") authHeader: String
    ): Response<Void>

}