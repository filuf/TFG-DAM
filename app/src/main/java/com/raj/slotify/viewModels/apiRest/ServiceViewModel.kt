package com.raj.slotify.viewModels.apiRest

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.raj.slotify.BuildConfig
import com.raj.slotify.dtos.service.CreateServiceRequest
import com.raj.slotify.dtos.service.CreateServiceResponse
import com.raj.slotify.dtos.service.CreateServiceScheduleRequest
import com.raj.slotify.dtos.service.CreateServiceScheduleResponse
import com.raj.slotify.dtos.service.ServiceSummary
import com.raj.slotify.services.retrofit.RetrofitInstance
import com.raj.slotify.services.retrofit.slotifyBackend.ServiceService
import retrofit2.Response
import java.util.UUID

class ServiceViewModel: ViewModel() {

    val appInDebug: Boolean = BuildConfig.DEBUG_MODE
    val baseUrl: String = if (appInDebug) BuildConfig.SPRING_TEST_URL else BuildConfig.SPRING_BASE_URL

    val endpoint: String = baseUrl + "services/"
    val service = RetrofitInstance.getApiService<ServiceService>(endpoint)

    private val _serviceCreated = MutableLiveData<Response<CreateServiceResponse>?>()
    var serviceCreated: LiveData<Response<CreateServiceResponse>?> = _serviceCreated

    private val _serviceWithSchedules = MutableLiveData<Response<ServiceSummary>?>()
    var serviceWithSchedules: LiveData<Response<ServiceSummary>?> = _serviceWithSchedules

    private val _serviceScheduleCreated = MutableLiveData<Response<CreateServiceScheduleResponse>?>()
    var serviceScheduleCreated: LiveData<Response<CreateServiceScheduleResponse>?> = _serviceScheduleCreated

    fun createService(authHeader: String, createServiceRequest: CreateServiceRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = service.createService(authHeader, createServiceRequest)
            if (!response.isSuccessful) {
                Log.e(
                    "Error en ServiceViewModel",
                    "Error intentando crear servicio: ${response.code()}: ${response.message()}"
                )
            }
            _serviceCreated.postValue(response)
        }
    }

    fun getServiceWithSchedules(serviceId: UUID, authHeader: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = service.getServiceSchedules(serviceId.toString(), authHeader)
            if (!response.isSuccessful) {
                Log.e(
                    "Error en ServiceViewModel",
                    "Error intentando obtener el horario del servicio: ${response.code()}: ${response.message()}"
                )
                _serviceWithSchedules.postValue(null)
            }
            _serviceWithSchedules.postValue(response)
        }
    }

    fun createServiceSchedule(authHeader: String, serviceId: String, createServiceScheduleRequest: CreateServiceScheduleRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = service.createServiceSchedule(authHeader, serviceId, createServiceScheduleRequest)
            if (!response.isSuccessful) {
                Log.e(
                    "Error en ServiceViewModel",
                    "Error intentando crear el horario del servicio: ${response.code()}: ${response.message()}"
                )
                _serviceScheduleCreated.postValue(null)
            }
            _serviceScheduleCreated.postValue(response)
        }
    }

}