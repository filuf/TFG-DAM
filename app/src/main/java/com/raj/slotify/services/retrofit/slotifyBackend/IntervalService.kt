package com.raj.slotify.services.retrofit.slotifyBackend

import com.raj.slotify.dtos.company.CreateIntervalRequest
import com.raj.slotify.dtos.company.CreateIntervalResponse
import retrofit2.Response
import retrofit2.http.*
import java.util.UUID

interface IntervalService {

    @POST(".")
    suspend fun createInterval(
        @Header("Authorization") authHeader: String,
        @Body createIntervalRequest: CreateIntervalRequest
    ): Response<CreateIntervalResponse>

    @DELETE("{intervalId}")
    suspend fun deleteInterval(
        @Header("Authorization") authHeader: String,
        @Path("intervalId") intervalId: UUID
    ): Response<Void>

}