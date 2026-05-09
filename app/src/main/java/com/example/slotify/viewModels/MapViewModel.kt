package com.example.slotify.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.slotify.fragments.mapsFragment.PlaceSuggestion
import com.example.slotify.services.retrofit.GoogleMapsApiService
import com.example.slotify.services.retrofit.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class MapViewModel: ViewModel() {

    private val service = RetrofitInstance.getApiService<GoogleMapsApiService>("https://nominatim.openstreetmap.org/")
    private var searchJob: Job? = null // JOB TO DELAY THE API CALL

    private val _placeSuggestions = MutableLiveData<List<PlaceSuggestion>>()
    val placeSuggestions: LiveData<List<PlaceSuggestion>> = _placeSuggestions

    private suspend fun getCords(place: String, format: String, userAgent: String): List<PlaceSuggestion> {

        return try {
            val response = service.getMap(place, format, userAgent)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                Log.e("API ERROR", "Código de respuesta: ${response.code()} - ${response.errorBody()?.string()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("API ERROR", "Fallo en la conexión: ${e.message}")
            emptyList()
        }
    }

    fun searchPlaces(place: String, format: String, userAgent: String) {
        searchJob?.cancel()

        // NOT SEARCH IF THE TEXT IS SHORT
        if (place.length < 3) {
            _placeSuggestions.value = emptyList()
            return
        }

        try {
            // START NEW TIMER
            searchJob = viewModelScope.launch(Dispatchers.IO) {
                delay(800)

                val suggestions = getCords(place, format, userAgent)
                _placeSuggestions.postValue(suggestions)
                Log.i("API_RESPONSE", "Response: $suggestions")
            }
        } catch (e: CancellationException) {
            // NORMAL CANCELLATION EVENT
        } catch (e: Exception) {
            Log.e("API_ERROR", "Error: ${e.message}")
        }
    }
}