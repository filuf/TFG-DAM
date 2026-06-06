package com.raj.slotify.services.retrofit.slotifyBackend

import com.raj.slotify.dtos.service.PatchScheduleRequest
import com.raj.slotify.dtos.service.ScheduleSummary
import retrofit2.Response
import retrofit2.http.*
import java.util.UUID

interface ScheduleService {

    @PATCH("schedules/{scheduleId}")
    suspend fun patchSchedule(
        @Path("scheduleId") scheduleId: UUID,
        @Header("Authorization") authHeader: String,
        @Body request: PatchScheduleRequest
    ): Response<ScheduleSummary>

}