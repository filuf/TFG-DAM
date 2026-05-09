package com.raj.slotify.viewModels.apiRest

import androidx.lifecycle.ViewModel
import com.raj.slotify.BuildConfig
import com.raj.slotify.services.retrofit.RetrofitInstance
import com.raj.slotify.services.retrofit.slotifyBackend.ServiceService

class ServiceViewModel: ViewModel() {

    val appInDebug: Boolean = BuildConfig.DEBUG
    val baseUrl: String = if (appInDebug) BuildConfig.SPRING_TEST_URL else BuildConfig.SPRING_BASE_URL

    val endpoint: String = baseUrl + "services/"
    val service = RetrofitInstance.getApiService<ServiceService>(endpoint)

}