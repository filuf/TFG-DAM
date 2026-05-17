package com.raj.slotify.viewModels.apiRest

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.raj.slotify.BuildConfig
import com.raj.slotify.dtos.company.CreateIntervalRequest
import com.raj.slotify.dtos.company.CreateIntervalResponse
import com.raj.slotify.services.retrofit.RetrofitInstance
import com.raj.slotify.services.retrofit.slotifyBackend.IntervalService
import retrofit2.Response
import java.util.UUID

class IntervalViewModel: ViewModel() {

    val appInDebug: Boolean = BuildConfig.DEBUG_MODE
    val baseUrl: String = if (appInDebug) BuildConfig.SPRING_TEST_URL else BuildConfig.SPRING_BASE_URL

    val endpoint: String = baseUrl + "intervals/"
    val service = RetrofitInstance.getApiService<IntervalService>(endpoint)

    private val _intervalCreated = MutableLiveData<Response<CreateIntervalResponse>?>()
    var intervalCreated: LiveData<Response<CreateIntervalResponse>?> = _intervalCreated

    private val _intervalDeletedResponse = MutableLiveData<Response<Void>?>()
    var intervalDeletedResponse: LiveData<Response<Void>?> = _intervalDeletedResponse


    fun createInterval(authHeader: String, createIntervalRequest: CreateIntervalRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = service.createInterval(authHeader, createIntervalRequest)
            if (!response.isSuccessful) {
                Log.e(
                    "Error en IntervalViewModel",
                    "Error intentando crear intervalo: ${response.code()}: ${response.message()}"
                )
            }
            _intervalCreated.postValue(response)
        }
    }

    fun deleteInterval(authHeader: String, intervalId: UUID) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = service.deleteInterval(authHeader, intervalId)
            if (!response.isSuccessful) {
                Log.e(
                    "Error en IntervalViewModel",
                    "Error intentando eliminar intervalo: ${response.code()}: ${response.message()}"
                )
            }
            _intervalDeletedResponse.postValue(response)
        }
    }

}