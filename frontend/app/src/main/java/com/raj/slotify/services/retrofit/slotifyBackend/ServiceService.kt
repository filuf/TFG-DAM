package com.raj.slotify.services.retrofit.slotifyBackend

import com.raj.slotify.dtos.service.CreateServiceRequest
import com.raj.slotify.dtos.service.CreateServiceResponse
import com.raj.slotify.dtos.service.CreateServiceScheduleRequest
import com.raj.slotify.dtos.service.CreateServiceScheduleResponse
import com.raj.slotify.dtos.service.ServiceSummary
import retrofit2.Response
import retrofit2.http.*

interface ServiceService {

    @POST("services")
    suspend fun createService(
        @Header("Authorization") authHeader: String,
        @Body createServiceRequest: CreateServiceRequest
    ): Response<CreateServiceResponse>

    @GET("services/{serviceId}/schedules")
    suspend fun getServiceSchedules(
        @Path("serviceId") serviceId: String,
        @Header("Authorization") authHeader: String
    ): Response<ServiceSummary>

    @POST("services/{serviceId}/schedules")
    suspend fun createServiceSchedule(
        @Path("serviceId") serviceId: String,
        @Header("Authorization") authHeader: String,
        @Body createServiceScheduleRequest: CreateServiceScheduleRequest
    ): Response<CreateServiceScheduleResponse>

}