package com.raj.slotify.viewModels.apiRest

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.raj.slotify.dtos.service.PatchScheduleRequest
import com.raj.slotify.dtos.service.ScheduleSummary
import com.raj.slotify.services.retrofit.RetrofitInstance
import com.raj.slotify.services.retrofit.slotifyBackend.ScheduleService
import retrofit2.Response
import java.util.UUID

class ScheduleViewModel: ViewModel() {

    private val service = RetrofitInstance.getService(ScheduleService::class.java)

    private val _scheduleUpdated = MutableLiveData<Response<ScheduleSummary>?>()
    var scheduleUpdated: MutableLiveData<Response<ScheduleSummary>?> = _scheduleUpdated

    private val _scheduleDeleted = MutableLiveData<Response<Unit>?>()
    var scheduleDeleted: MutableLiveData<Response<Unit>?> = _scheduleDeleted

    fun updateSchedule(scheduleId: UUID, authHeader: String, patchScheduleRequest: PatchScheduleRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = service.patchSchedule(scheduleId, authHeader, patchScheduleRequest)
                if (!response.isSuccessful) {
                    Log.e(
                        "Error en ScheduleViewModel",
                        "Error intentando actualizar horario: ${response.code()}: ${response.message()}"
                    )
                }
                _scheduleUpdated.postValue(response)
            } catch (e: Exception) {
                Log.e("ScheduleViewModel", "Excepción al actualizar horario: ${e.message}")
                _scheduleUpdated.postValue(null)
            }
        }
    }

    fun deleteSchedule(scheduleId: UUID, authHeader: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = service.deleteSchedule(scheduleId, authHeader)
                if (!response.isSuccessful) {
                    Log.e(
                        "Error en ScheduleViewModel",
                        "Error intentando eliminar horario: ${response.code()}: ${response.message()}"
                    )
                }
                _scheduleDeleted.postValue(response)
            } catch (e: Exception) {
                Log.e("ScheduleViewModel", "Excepción al eliminar horario: ${e.message}")
                _scheduleDeleted.postValue(null)
            }
        }
    }

}