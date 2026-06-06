package com.raj.slotify.viewModels.apiRest

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.raj.slotify.dtos.company.GetServicesResponse
import com.raj.slotify.models.api.PageResponse
import com.raj.slotify.services.retrofit.RetrofitInstance
import com.raj.slotify.services.retrofit.slotifyBackend.CompanyService
import retrofit2.Response
import java.util.UUID

class CompanyViewModel: ViewModel() {

    val service = RetrofitInstance.getService(CompanyService::class.java)

    private val _listOfServices = MutableLiveData<Response<PageResponse<GetServicesResponse>>?>(null)
    var listOfServices: LiveData<Response<PageResponse<GetServicesResponse>>?> = _listOfServices

    fun getServicesByCompanyId(companyId: UUID, authHeader: String, fetchMode: String? = null, page: Int? = null, sortBy: String? = null, order: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = service.getServicesByCompanyId(companyId, authHeader, fetchMode, page, sortBy, order)
                if (!response.isSuccessful) {
                    Log.e("CompanyViewModel", "Error obteniendo servicios: ${response.code()}: ${response.message()}")
                }
                _listOfServices.postValue(response)
            } catch (e: Exception) {
                Log.e("CompanyViewModel", "Excepción al obtener servicios: ${e.message}")
                _listOfServices.postValue(null)
            }
        }
    }

}