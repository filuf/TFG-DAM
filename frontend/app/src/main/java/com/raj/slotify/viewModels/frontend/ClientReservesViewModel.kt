package com.raj.slotify.viewModels.frontend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.raj.slotify.dtos.company.GetCompanyResponse
import com.raj.slotify.dtos.company.GetServicesResponse
import com.raj.slotify.dtos.reserves.ReserveSummary
import java.time.LocalDateTime

class ClientReservesViewModel: ViewModel() {

    private val _reserves = MutableLiveData<MutableList<ReserveSummary>>()
    private val _lastReserveSelected = MutableLiveData<ReserveSummary>()
    private val _companyToReserve = MutableLiveData<GetCompanyResponse>()
    private val _serviceToReserve = MutableLiveData<GetServicesResponse>()
    private val _categoryToReserve = MutableLiveData<String>()
    private val _dateTimeReserve = MutableLiveData<LocalDateTime?>()

    val reserves: LiveData<MutableList<ReserveSummary>> = _reserves
    val lastReserveSelected: LiveData<ReserveSummary> = _lastReserveSelected
    val serviceToReserve: LiveData<GetServicesResponse> = _serviceToReserve
    val companyToReserve: LiveData<GetCompanyResponse> = _companyToReserve
    val categoryToReserve: LiveData<String> = _categoryToReserve
    val dateTimeReserve: LiveData<LocalDateTime?> = _dateTimeReserve

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

    fun setCompanyToReserve(companyToReserve: GetCompanyResponse) {
        _companyToReserve.postValue(companyToReserve)
    }

    fun setServiceToReserve(serviceToReserve: GetServicesResponse) {
        _serviceToReserve.postValue(serviceToReserve)
    }

    fun setCategoryToReserve(categoryToReserve: String) {
        _categoryToReserve.postValue(categoryToReserve)
    }

    fun setDateTimeReserve(dateTimeReserve: LocalDateTime?) {
        _dateTimeReserve.postValue(dateTimeReserve)
    }

}