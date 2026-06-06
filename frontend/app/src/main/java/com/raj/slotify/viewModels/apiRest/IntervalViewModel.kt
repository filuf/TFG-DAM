package com.raj.slotify.viewModels.apiRest

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.raj.slotify.dtos.company.CreateIntervalRequest
import com.raj.slotify.dtos.company.CreateIntervalResponse
import com.raj.slotify.dtos.company.IntervalSummary
import com.raj.slotify.services.retrofit.RetrofitInstance
import com.raj.slotify.services.retrofit.slotifyBackend.IntervalService
import retrofit2.Response
import java.util.UUID

class IntervalViewModel : ViewModel() {

    val service = RetrofitInstance.getService(IntervalService::class.java)

    private val _intervals = MutableLiveData<Response<List<IntervalSummary>>?>()
    val intervals: LiveData<Response<List<IntervalSummary>>?> = _intervals

    private val _intervalCreated = MutableLiveData<Response<CreateIntervalResponse>?>()
    val intervalCreated: LiveData<Response<CreateIntervalResponse>?> = _intervalCreated

    private val _intervalDeletedResponse = MutableLiveData<Response<Void>?>()
    val intervalDeletedResponse: LiveData<Response<Void>?> = _intervalDeletedResponse

    fun getIntervals(authHeader: String, fetchMode: String = "ALL") {
        viewModelScope.launch(Dispatchers.IO) {
            val response = service.getIntervals(authHeader, fetchMode)
            if (!response.isSuccessful) {
                Log.e("IntervalViewModel", "Error obteniendo intervalos: ${response.code()}: ${response.message()}")
            }
            _intervals.postValue(response)
        }
    }

    fun createInterval(authHeader: String, createIntervalRequest: CreateIntervalRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = service.createInterval(authHeader, createIntervalRequest)
            if (!response.isSuccessful) {
                Log.e("IntervalViewModel", "Error creando intervalo: ${response.code()}: ${response.message()}")
            }
            _intervalCreated.postValue(response)
        }
    }

    fun deleteInterval(authHeader: String, intervalId: UUID) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = service.deleteInterval(authHeader, intervalId)
            if (!response.isSuccessful) {
                Log.e("IntervalViewModel", "Error eliminando intervalo: ${response.code()}: ${response.message()}")
            }
            _intervalDeletedResponse.postValue(response)
        }
    }

    fun clearCreated() { _intervalCreated.postValue(null) }
    fun clearDeleted() { _intervalDeletedResponse.postValue(null) }
}