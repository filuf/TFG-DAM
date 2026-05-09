package com.example.slotify.services.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.reflect.KClass

class RetrofitInstance {
    companion object {
        fun getRetrofit(url: String): Retrofit {
            val retrofit by lazy {
                Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }

        @JvmStatic
        inline fun <reified T : Any> getApiService(url: String): T {
            return getRetrofit(url).create(T::class.java)
        }
    }
}