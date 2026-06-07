package com.raj.slotify.services.retrofit.slotifyBackend

import com.raj.slotify.dtos.user.UserSummary
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part
import retrofit2.http.Path
import java.util.UUID

interface UserService {

    @GET("users/{userId}")
    suspend fun getUser(
        @Path("userId") userId: UUID,
        @Header("Authorization") authHeader: String
    ): Response<UserSummary>

    @Multipart
    @PATCH("users")
    suspend fun patchUser(
        @Header("Authorization") authHeader: String,
        @Part file: MultipartBody.Part?,
        @Part("request") request: RequestBody
    ): Response<UserSummary>

}