package com.raj.slotify.viewModels.apiRest

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.raj.slotify.dtos.reserves.TimeIntervalDTO
import com.raj.slotify.dtos.service.CreateServiceRequest
import com.raj.slotify.dtos.service.CreateServiceResponse
import com.raj.slotify.dtos.service.CreateServiceScheduleRequest
import com.raj.slotify.dtos.service.CreateServiceScheduleResponse
import com.raj.slotify.dtos.service.ServiceSummary
import com.raj.slotify.services.retrofit.RetrofitInstance
import com.raj.slotify.services.retrofit.slotifyBackend.ServiceService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.util.UUID

class ServiceViewModel: ViewModel() {

    private val service = RetrofitInstance.getService(ServiceService::class.java)

    private val _serviceCreated = MutableLiveData<Response<CreateServiceResponse>?>()
    var serviceCreated: LiveData<Response<CreateServiceResponse>?> = _serviceCreated

    private val _serviceWithSchedules = MutableLiveData<Response<ServiceSummary>?>()
    var serviceWithSchedules: LiveData<Response<ServiceSummary>?> = _serviceWithSchedules

    private val _serviceScheduleCreated = MutableLiveData<Response<CreateServiceScheduleResponse>?>()
    var serviceScheduleCreated: LiveData<Response<CreateServiceScheduleResponse>?> = _serviceScheduleCreated

    private val _serviceSlotsAvailable = MutableLiveData<Response<List<TimeIntervalDTO>>?>()
    var serviceSlotsAvailable: LiveData<Response<List<TimeIntervalDTO>>?> = _serviceSlotsAvailable

    private val _servicePatched = MutableLiveData<Response<ServiceSummary>?>()
    var servicePatched: LiveData<Response<ServiceSummary>?> = _servicePatched

    fun createService(authHeader: String, createServiceRequest: CreateServiceRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = service.createService(authHeader, createServiceRequest)
                if (!response.isSuccessful) {
                    Log.e("ServiceViewModel", "Error creando servicio: ${response.code()}: ${response.message()}")
                }
                _serviceCreated.postValue(response)
            } catch (e: Exception) {
                Log.e("ServiceViewModel", "Excepción al crear servicio: ${e.message}")
                _serviceCreated.postValue(null)
            }
        }
    }

    fun patchService(serviceId: UUID, authHeader: String, file: MultipartBody.Part?, patchServiceRequest: RequestBody) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = service.patchService(
                    serviceId,
                    authHeader,
                    file,
                    patchServiceRequest
                )
                if (!response.isSuccessful) {
                    Log.e("ServiceViewModel", "Error actualizando servicio: ${response.code()}: ${response.message()}")
                }
                _servicePatched.postValue(response)
            } catch (e: Exception) {
                Log.e("ServiceViewModel", "Excepción al actualizar servicio: ${e.message}")
                _servicePatched.postValue(null)
            }
        }
    }

    fun getServiceWithSchedules(serviceId: UUID, authHeader: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = service.getServiceSchedules(serviceId.toString(), authHeader)
                if (!response.isSuccessful) {
                    Log.e(
                        "Error en ServiceViewModel",
                        "Error intentando obtener el horario del servicio: ${response.code()}: ${response.message()}"
                    )
                }
                _serviceWithSchedules.postValue(response)
            } catch (e: Exception) {
                Log.e("ServiceViewModel", "Excepción al obtener el horario del servicio: ${e.message}")
                _serviceWithSchedules.postValue(null)
            }
        }
    }

    fun createServiceSchedule(authHeader: String, serviceId: String, createServiceScheduleRequest: CreateServiceScheduleRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = service.createServiceSchedule(serviceId, authHeader, createServiceScheduleRequest)
                if (!response.isSuccessful) {
                    Log.e(
                        "Error en ServiceViewModel",
                        "Error intentando crear el horario del servicio: ${response.code()}: ${response.message()}"
                    )
                }
                _serviceScheduleCreated.postValue(response)
            } catch (e: Exception) {
                Log.e("ServiceViewModel", "Excepción al crear el horario del servicio: ${e.message}")
                _serviceScheduleCreated.postValue(null)
            }
        }
    }

    fun getServiceSlotsAvailable(serviceId: UUID, authHeader: String, date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = service.getServiceSlotsAvailable(serviceId, authHeader, date)
                if (!response.isSuccessful) {
                    Log.e(
                        "Error en ServiceViewModel",
                        "Error intentando obtener los intervalos disponibles del servicio: ${response.code()}: ${response.message()}"
                    )
                }
                _serviceSlotsAvailable.postValue(response)
            } catch (e: Exception) {
                Log.e("ServiceViewModel", "Excepción al obtener los intervalos disponibles del servicio: ${e.message}")
                _serviceSlotsAvailable.postValue(null)
            }
        }
    }

    fun clearSlots() {
        _serviceSlotsAvailable.postValue(null)
    }

}