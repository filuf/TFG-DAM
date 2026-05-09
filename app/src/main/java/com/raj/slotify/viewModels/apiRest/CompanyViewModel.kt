package com.raj.slotify.viewModels.apiRest

import androidx.lifecycle.ViewModel
import com.raj.slotify.BuildConfig
import com.raj.slotify.services.retrofit.RetrofitInstance
import com.raj.slotify.services.retrofit.slotifyBackend.CompanyService

class CompanyViewModel: ViewModel() {

    val appInDebug: Boolean = BuildConfig.DEBUG
    val baseUrl: String = if (appInDebug) BuildConfig.SPRING_TEST_URL else BuildConfig.SPRING_BASE_URL

    val endpoint: String = baseUrl + "companies/"
    val service = RetrofitInstance.getApiService<CompanyService>(endpoint)

}