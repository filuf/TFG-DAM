package com.raj.slotify.services.retrofit.slotifyBackend

import com.raj.slotify.dtos.company.CreateIntervalRequest
import com.raj.slotify.dtos.company.CreateIntervalResponse
import com.raj.slotify.dtos.company.IntervalSummary
import com.raj.slotify.dtos.company.UpdateIntervalRequest
import com.raj.slotify.dtos.company.UpdateIntervalResponse
import retrofit2.Response
import retrofit2.http.*
import java.util.UUID

interface IntervalService {

    @GET("intervals")
    suspend fun getIntervals(
        @Header("Authorization") authHeader: String,
        @Query("fetchMode") fetchMode: String = "ALL"
    ): Response<List<IntervalSummary>>

    @POST("intervals")
    suspend fun createInterval(
        @Header("Authorization") authHeader: String,
        @Body createIntervalRequest: CreateIntervalRequest
    ): Response<CreateIntervalResponse>

    @PUT("intervals/{intervalId}")
    suspend fun updateInterval(
        @Header("Authorization") authHeader: String,
        @Path("intervalId") intervalId: UUID,
        @Body updateIntervalRequest: UpdateIntervalRequest
    ): Response<UpdateIntervalResponse>

    @DELETE("intervals/{intervalId}")
    suspend fun deleteInterval(
        @Header("Authorization") authHeader: String,
        @Path("intervalId") intervalId: UUID
    ): Response<Void>

}