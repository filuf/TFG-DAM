package com.raj.slotify.viewModels.frontend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.raj.slotify.dtos.reserves.ReserveSummary
import com.raj.slotify.dtos.reserves.UserReserveSummary

class ClientReservesViewModel: ViewModel() {

    private val _reserves = MutableLiveData<MutableList<ReserveSummary>>()
    private val _lastReserveSelected = MutableLiveData<ReserveSummary>()
    private val _serviceToReserve = MutableLiveData<UserReserveSummary>()

    private val _categoryToReserve = MutableLiveData<String>()

    val reserves: LiveData<MutableList<ReserveSummary>> = _reserves
    val lastReserveSelected: LiveData<ReserveSummary> = _lastReserveSelected
    val serviceToReserve: LiveData<UserReserveSummary> = _serviceToReserve
    val categoryToReserve: LiveData<String> = _categoryToReserve

    fun setReserves(reserves: MutableList<ReserveSummary>) {
        _reserves.postValue(reserves)
    }

    fun addReserves(newList: List<ReserveSummary>) {
        val currentList = _reserves.value ?: mutableListOf()
        currentList.addAll(newList)
        _reserves.value = currentList
    }

    fun setLastReserveSelected(lastReserveSelected: ReserveSummary) {
        _lastReserveSelected.postValue(lastReserveSelected)
    }

    fun setServiceToReserve(serviceToReserve: UserReserveSummary) {
        _serviceToReserve.postValue(serviceToReserve)
    }

    fun setCategoryToReserve(categoryToReserve: String) {
        _categoryToReserve.postValue(categoryToReserve)
    }

}