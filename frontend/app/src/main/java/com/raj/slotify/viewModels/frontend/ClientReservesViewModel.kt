package com.raj.slotify.viewModels.frontend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.raj.slotify.dtos.company.GetServicesResponse
import com.raj.slotify.dtos.company.SearchCompaniesResponse
import com.raj.slotify.dtos.reserves.ReserveSummary
import com.raj.slotify.models.api.PageResponse
import java.time.LocalDateTime

class ClientReservesViewModel: ViewModel() {

    private val _reserves = MutableLiveData<MutableList<ReserveSummary>>()
    private val _reservesPage = MutableLiveData<PageResponse<ReserveSummary>>()
    private val _lastReserveSelected = MutableLiveData<ReserveSummary>()
    private val _fetchType = MutableLiveData<String>()
    private val _companyToReserve = MutableLiveData<SearchCompaniesResponse>()
    private val _serviceToReserve = MutableLiveData<GetServicesResponse>()
    private val _categoryToReserve = MutableLiveData<String>()
    private val _dateTimeReserve = MutableLiveData<LocalDateTime?>()

    val reserves: LiveData<MutableList<ReserveSummary>> = _reserves
    val reservesPage: LiveData<PageResponse<ReserveSummary>> = _reservesPage
    val fetchType: LiveData<String> = _fetchType
    val lastReserveSelected: LiveData<ReserveSummary> = _lastReserveSelected
    val serviceToReserve: LiveData<GetServicesResponse> = _serviceToReserve
    val companyToReserve: LiveData<SearchCompaniesResponse> = _companyToReserve
    val categoryToReserve: LiveData<String> = _categoryToReserve
    val dateTimeReserve: LiveData<LocalDateTime?> = _dateTimeReserve

    fun setReserves(reserves: MutableList<ReserveSummary>) {
        _reserves.postValue(reserves)
    }

    fun setReservesPage(reservesPage: PageResponse<ReserveSummary>) {
        _reservesPage.postValue(reservesPage)
    }

    fun setFetchType(fetchType: String) {
        _fetchType.postValue(fetchType)
    }

    fun addReserves(newList: List<ReserveSummary>) {
        val currentList = _reserves.value ?: mutableListOf()
        currentList.addAll(newList)
        _reserves.value = currentList
    }

    fun setLastReserveSelected(lastReserveSelected: ReserveSummary) {
        _lastReserveSelected.postValue(lastReserveSelected)
    }

    fun setCompanyToReserve(companyToReserve: SearchCompaniesResponse) {
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