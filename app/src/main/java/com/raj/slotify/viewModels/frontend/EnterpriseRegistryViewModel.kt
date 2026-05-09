package com.raj.slotify.viewModels.frontend

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.raj.slotify.dtos.maps.PlaceSuggestion

class EnterpriseRegistryViewModel: ViewModel() {

    private val _ubicationCords = MutableLiveData<PlaceSuggestion?>()
    private val _concurrentServices = MutableLiveData<Int>()

    fun setUbicationCords(placeSuggestion: PlaceSuggestion?) {
        _ubicationCords.postValue(placeSuggestion)
    }

    fun setConcurrentServices(concurrentServices: Int) {
        _concurrentServices.postValue(concurrentServices)
    }

}