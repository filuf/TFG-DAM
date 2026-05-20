package com.raj.slotify.services.retrofit

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.raj.slotify.BuildConfig
import com.raj.slotify.dtos.reserves.ReserveSummary
import com.raj.slotify.tools.LocalDateTimeAdapter
import com.raj.slotify.tools.LocalTimeAdapter
import com.raj.slotify.tools.ReserveSummaryDeserializer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.LocalTime

object RetrofitInstance {

    private val baseUrl: String = if (BuildConfig.DEBUG_MODE)
        BuildConfig.SPRING_TEST_URL else BuildConfig.SPRING_BASE_URL

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .registerTypeAdapter(LocalTime::class.java, LocalTimeAdapter())
        .registerTypeAdapter(ReserveSummary::class.java, ReserveSummaryDeserializer())
        .create()

    @JvmStatic
    fun <T> getService(serviceClass: Class<T>, url: String?=null): T {

        var endpoint: String = if (!url.isNullOrEmpty()) url else baseUrl
        endpoint = if (endpoint.endsWith("/")) endpoint else "$endpoint/"

        return Retrofit.Builder()
            .baseUrl(endpoint)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(serviceClass)
    }

}