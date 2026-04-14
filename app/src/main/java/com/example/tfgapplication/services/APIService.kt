package com.example.tfgapplication.services

import com.example.tfgapplication.models.SongResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface APIService {
    @GET("canciones")
    suspend fun getSongs(): Response<List<SongResponse>>

    @GET("cancion/{codigo}")
    suspend fun getSongByID(@Path("codigo") codigo: String): Response<SongResponse>

    @POST("cancion")
    suspend fun postSong(@Body song: SongResponse): Response<SongResponse>

    @PATCH("cancion/update")
    suspend fun updateCancion(@Body song: SongResponse): Response<SongResponse>

    @DELETE("cancion/{codigo}")
    suspend fun deleteCancionByCodigo(@Path("codigo") codigo: String): Response<SongResponse>

}