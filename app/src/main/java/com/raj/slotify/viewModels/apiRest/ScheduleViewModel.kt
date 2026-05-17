package com.raj.slotify.viewModels.apiRest

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.raj.slotify.BuildConfig
import com.raj.slotify.dtos.service.PatchScheduleRequest
import com.raj.slotify.dtos.service.ScheduleSummary
import com.raj.slotify.services.retrofit.RetrofitInstance
import com.raj.slotify.services.retrofit.slotifyBackend.ScheduleService
import retrofit2.Response
import java.util.UUID

class ScheduleViewModel: ViewModel() {

    val appInDebug: Boolean = BuildConfig.DEBUG_MODE
    val baseUrl: String = if (appInDebug) BuildConfig.SPRING_TEST_URL else BuildConfig.SPRING_BASE_URL

    val endpoint: String = baseUrl + "schedules/"
    val service = RetrofitInstance.getApiService<ScheduleService>(endpoint)

    private val _scheduleUpdated = MutableLiveData<Response<ScheduleSummary>?>()
    var scheduleUpdated: MutableLiveData<Response<ScheduleSummary>?> = _scheduleUpdated

    fun updateSchedule(scheduleId: UUID, authHeader: String, patchScheduleRequest: PatchScheduleRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = service.patchSchedule(scheduleId, authHeader, patchScheduleRequest)
            if (!response.isSuccessful) {
                Log.e(
                    "Error en ScheduleViewModel",
                    "Error intentando actualizar horario: ${response.code()}: ${response.message()}"
                )
            }
            _scheduleUpdated.postValue(response)
        }
    }

}