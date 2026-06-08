package com.raj.slotify.viewModels.apiRest

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.raj.slotify.dtos.reserves.CreateReserveRequest
import com.raj.slotify.dtos.reserves.CreateReserveResponse
import com.raj.slotify.dtos.reserves.ReserveSummary
import com.raj.slotify.models.api.PageResponse
import com.raj.slotify.services.retrofit.RetrofitInstance
import com.raj.slotify.services.retrofit.slotifyBackend.ReservesService
import retrofit2.Response
import java.util.UUID

class ReservesViewModel: ViewModel() {

    private val service = RetrofitInstance.getService(ReservesService::class.java)

    private val _reservesSummary = MutableLiveData<Response<PageResponse<ReserveSummary>>?>()
    var reservesSummary: LiveData<Response<PageResponse<ReserveSummary>>?> = _reservesSummary

    private val _reserveCreate = MutableLiveData<Response<CreateReserveResponse>?>()
    var reserveCrated: LiveData<Response<CreateReserveResponse>?> = _reserveCreate

    private val _reserveSearched = MutableLiveData<Response<ReserveSummary>?>()
    var reserveSearched: LiveData<Response<ReserveSummary>?> = _reserveSearched

    private val _reserveCanceledResponse = MutableLiveData<Response<Void>?>()
    var reserveCanceledResponse: LiveData<Response<Void>?> = _reserveCanceledResponse

    fun getReserveById(reserveId: UUID, authHeader: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = service.getReserve(reserveId, authHeader)
                if (!response.isSuccessful) {
                    Log.e(
                        "Error en ReservesViewModel",
                        "Error intentando obtener reserva: ${response.code()}: ${response.message()} - ${response.body()}"
                    )
                }
                _reserveSearched.postValue(response)
            } catch (e: Exception) {
                Log.e("ReservesViewModel", "Excepción al obtener reserva: ${e.message}")
                _reserveSearched.postValue(null)
            }
        }
    }

    fun getReserves(authHeader: String, page: Int?, size: Int?, sort: String?, fetchType: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            _reservesSummary.postValue(null)

            try {
                val response = service.getReserves(authHeader, page, size, sort, fetchType)
                Log.i("DEBUG GET RESERVES", "url: ")
                if (!response.isSuccessful) {
                    Log.e(
                        "Error en ReservesViewModel",
                        "Error intentando obtener reservas: ${response.code()}: ${response.message()} - ${response.body()}"
                    )
                }
                _reservesSummary.postValue(response)
            } catch (e: Exception) {
                Log.e("ReservesViewModel", "Excepción al obtener reservas: ${e.message}")
                _reservesSummary.postValue(null)
            }
        }
    }

    fun createReserve(authHeader: String, createReserveRequest: CreateReserveRequest) {
        _reserveCreate.value = null
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = service.createReserve(authHeader, createReserveRequest)
                if (!response.isSuccessful) {
                    Log.e(
                        "Error en ReservesViewModel",
                        "Error intentando crear reserva: ${response.code()}: ${response.message()} - ${response.body()}"
                    )
                }
                _reserveCreate.postValue(response)
            } catch (e: Exception) {
                Log.e("ReservesViewModel", "Excepción al crear reserva: ${e.message}")
                _reserveCreate.postValue(null)
            }
        }
    }

    fun cancelReserve(reserveId: UUID, authHeader: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = service.cancelReserve(reserveId, authHeader)
                if (!response.isSuccessful) {
                    Log.e(
                        "Error en ReservesViewModel",
                        "Error intentando cancelar reserva: ${response.code()}: ${response.message()} - ${response.body()}"
                    )
                }
                _reserveCanceledResponse.postValue(response)
            } catch (e: Exception) {
                Log.e("ReservesViewModel", "Excepción al cancelar reserva: ${e.message}")
                _reserveCanceledResponse.postValue(null)
            }
        }
    }

}