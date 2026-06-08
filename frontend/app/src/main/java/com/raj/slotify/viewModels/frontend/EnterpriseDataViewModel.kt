package com.raj.slotify.viewModels.frontend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.raj.slotify.dtos.maps.PlaceSuggestion
import com.raj.slotify.dtos.service.ScheduleSummary

class EnterpriseDataViewModel: ViewModel() {

    private val _ubicationPlaceSuggestion = MutableLiveData<PlaceSuggestion?>()
    val ubicationPlaceSuggestion: LiveData<PlaceSuggestion?> = _ubicationPlaceSuggestion
    private val _concurrentServices = MutableLiveData<Int>()
    val concurrentServices: LiveData<Int> = _concurrentServices
    private val _description = MutableLiveData<String>()
    val description: LiveData<String> = _description

    private val _serviceId = MutableLiveData<String>()
    val serviceId: LiveData<String> = _serviceId

    private val _schedulesOfService = MutableLiveData<List<ScheduleSummary>?>()
    val schedulesOfService: LiveData<List<ScheduleSummary>?> = _schedulesOfService

    fun setUbicationCords(placeSuggestion: PlaceSuggestion?) {
        _ubicationPlaceSuggestion.postValue(placeSuggestion)
    }

    fun setConcurrentServices(concurrentServices: Int) {
        _concurrentServices.postValue(concurrentServices)
    }

    fun setDescription(description: String) {
        _description.postValue(description)
    }

    fun setSchedulesOfService(schedules: List<ScheduleSummary>) {
        _schedulesOfService.postValue(schedules)
    }

}