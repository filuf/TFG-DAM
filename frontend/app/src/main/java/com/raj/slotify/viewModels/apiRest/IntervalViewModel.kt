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
import com.raj.slotify.services.retrofit.RetrofitInstance
import com.raj.slotify.services.retrofit.slotifyBackend.IntervalService
import retrofit2.Response
import java.util.UUID

class IntervalViewModel: ViewModel() {

    private val service = RetrofitInstance.getService(IntervalService::class.java)

    private val _intervalCreated = MutableLiveData<Response<CreateIntervalResponse>?>()
    var intervalCreated: LiveData<Response<CreateIntervalResponse>?> = _intervalCreated

    private val _intervalDeletedResponse = MutableLiveData<Response<Void>?>()
    var intervalDeletedResponse: LiveData<Response<Void>?> = _intervalDeletedResponse


    fun createInterval(authHeader: String, createIntervalRequest: CreateIntervalRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = service.createInterval(authHeader, createIntervalRequest)
                if (!response.isSuccessful) {
                    Log.e(
                        "Error en IntervalViewModel",
                        "Error intentando crear intervalo: ${response.code()}: ${response.message()}"
                    )
                }
                _intervalCreated.postValue(response)
            } catch (e: Exception) {
                Log.e("Error en IntervalViewModel", "Error intentando crear intervalo: ${e.message}")
                _intervalCreated.postValue(null)
            }

        }
    }

    fun deleteInterval(authHeader: String, intervalId: UUID) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = service.deleteInterval(authHeader, intervalId)
                if (!response.isSuccessful) {
                    Log.e(
                        "Error en IntervalViewModel",
                        "Error intentando eliminar intervalo: ${response.code()}: ${response.message()}"
                    )
                }
                _intervalDeletedResponse.postValue(response)
            } catch (e: Exception) {
                Log.e("Error en IntervalViewModel", "Error intentando eliminar intervalo: ${e.message}")
                _intervalDeletedResponse.postValue(null)
            }
        }
    }

}