package com.raj.slotify.viewModels.apiRest

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.raj.slotify.BuildConfig
import com.raj.slotify.dtos.reserves.CreateReserveRequest
import com.raj.slotify.dtos.reserves.CreateReserveResponse
import com.raj.slotify.dtos.reserves.ReserveSummary
import com.raj.slotify.models.api.PageResponse
import com.raj.slotify.services.retrofit.RetrofitInstance
import com.raj.slotify.services.retrofit.slotifyBackend.ReservesService
import retrofit2.Response
import java.util.UUID

class ReservesViewModel: ViewModel() {

    val appInDebug: Boolean = BuildConfig.DEBUG
    val baseUrl: String = if (appInDebug) BuildConfig.SPRING_TEST_URL else BuildConfig.SPRING_BASE_URL

    val endpoint: String = baseUrl + "reserves/"
    val service = RetrofitInstance.getApiService<ReservesService>(endpoint)

    private val _reservesSummary = MutableLiveData<Response<PageResponse<ReserveSummary>>?>()
    var reservesSummary: LiveData<Response<PageResponse<ReserveSummary>>?> = _reservesSummary

    private val _reserveCreated = MutableLiveData<Response<CreateReserveResponse>?>()
    var reserveCrated: LiveData<Response<CreateReserveResponse>?> = _reserveCreated

    private val _reserveCanceledResponse = MutableLiveData<Response<Void>?>()
    var reserveCanceledResponse: LiveData<Response<Void>?> = _reserveCanceledResponse

    fun getReserves(authHeader: String, page: Int, size: Int, sort: String?, fetchType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = service.getReserves(authHeader, page, size, sort, fetchType)
            if (!response.isSuccessful) {
                Log.e(
                    "Error en ReservesViewModel",
                    "Error intentando obtener reservas: ${response.code()}: ${response.message()}"
                )
            }
            _reservesSummary.postValue(response)
        }
    }

    fun createReserve(authHeader: String, createReserveRequest: CreateReserveRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = service.createReserve(authHeader, createReserveRequest)
            if (!response.isSuccessful) {
                Log.e(
                    "Error en ReservesViewModel",
                    "Error intentando crear reserva: ${response.code()}: ${response.message()}"
                )
            }
            _reserveCreated.postValue(response)
        }
    }

    fun cancelReserve(reserveId: UUID, authHeader: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = service.cancelReserve(reserveId, authHeader)
            if (!response.isSuccessful) {
                Log.e(
                    "Error en ReservesViewModel",
                    "Error intentando cancelar reserva: ${response.code()}: ${response.message()}"
                )
            }
            _reserveCanceledResponse.postValue(response)
        }
    }

}