package com.raj.slotify.viewModels.apiRest

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.raj.slotify.BuildConfig
import com.raj.slotify.dtos.company.GetServicesResponse
import com.raj.slotify.services.retrofit.RetrofitInstance
import com.raj.slotify.services.retrofit.slotifyBackend.CompanyService
import retrofit2.Response
import java.util.Optional
import java.util.UUID

class CompanyViewModel: ViewModel() {

    val appInDebug: Boolean = BuildConfig.DEBUG
    val baseUrl: String = if (appInDebug) BuildConfig.SPRING_TEST_URL else BuildConfig.SPRING_BASE_URL

    val endpoint: String = baseUrl + "companies/"
    val service = RetrofitInstance.getApiService<CompanyService>(endpoint)

    private val _listOfServices = MutableLiveData<Response<List<GetServicesResponse>>?>()
    var listOfServices: LiveData<Response<List<GetServicesResponse>>?> = _listOfServices

    fun getServicesByCompanyId(companyId: UUID, place: String, page: Int, sortBy: String, order: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = service.getServicesByCompanyId(companyId, place, page, sortBy, order)
            if (!response.isSuccessful) {
                Log.e(
                    "Error en CompanyViewModel",
                    "Error intentando obtener servicios: ${response.code()}: ${response.message()}"
                )
            }
            _listOfServices.postValue(response)
        }
    }

}