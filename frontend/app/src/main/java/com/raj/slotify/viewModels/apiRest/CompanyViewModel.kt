package com.raj.slotify.viewModels.apiRest

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raj.slotify.dtos.company.GetCompanyResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.raj.slotify.dtos.company.GetServicesResponse
import com.raj.slotify.dtos.company.SearchCompaniesResponse
import com.raj.slotify.models.api.PageResponse
import com.raj.slotify.services.retrofit.RetrofitInstance
import com.raj.slotify.services.retrofit.slotifyBackend.CompanyService
import retrofit2.Response
import java.util.UUID

class CompanyViewModel: ViewModel() {

    private val service = RetrofitInstance.getService(CompanyService::class.java)

    private val _companies = MutableLiveData<Response<PageResponse<SearchCompaniesResponse>>?>(null)
    private val _company = MutableLiveData<Response<GetCompanyResponse>?>(null)
    private val _listOfServices = MutableLiveData<Response<PageResponse<GetServicesResponse>>?>(null)

    var companies: LiveData<Response<PageResponse<SearchCompaniesResponse>>?> = _companies
    var company: LiveData<Response<GetCompanyResponse>?> = _company
    var listOfServices: LiveData<Response<PageResponse<GetServicesResponse>>?> = _listOfServices

    fun getCompanies(authHeader: String, q: String, page: Int = 0) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.i("CompanyViewModel", "getCompanies called with authHeader: $authHeader")
                Log.i("CompanyViewModel", "getCompanies called with q: $q")
                Log.i("CompanyViewModel", "getCompanies called with page: $page")

                val response = service.getCompaniesByQuery(authHeader, q, page)
                if (!response.isSuccessful) {
                    Log.e("CompanyViewModel", "Error obteniendo empresas: ${response.code()}: ${response.message()}")
                }
                _companies.postValue(response)
            } catch (e: Exception) {
                Log.e("CompanyViewModel", "Excepción al obtener empresas: ${e.message}")
                _companies.postValue(null)
            }
        }
    }


    fun getCompanyById(companyId: UUID, authHeader: String, fetchMode: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = service.getCompanyById(companyId, authHeader, fetchMode)
                if (!response.isSuccessful) {
                    Log.e("CompanyViewModel", "Error obteniendo empresa: ${response.code()}: ${response.message()}")
                }
                _company.postValue(response)
            } catch (e: Exception) {
                Log.e("CompanyViewModel", "Excepción al obtener empresa: ${e.message}")
                _company.postValue(null)
            }
        }
    }

    fun getServicesByCompanyId(companyId: UUID, authHeader: String, fetchMode: String? = null, page: Int? = null, sortBy: String? = null, order: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = service.getServicesByCompanyId(companyId, authHeader, fetchMode, page, sortBy, order)
                if (!response.isSuccessful) {
                    Log.e("CompanyViewModel", "Error obteniendo servicios: ${response.code()}: ${response.message()}")
                }
                _listOfServices.postValue(response)
            } catch (e: Exception) {
                Log.e("CompanyViewModel", "Excepción al obtener servicios: ${e.message}")
                _listOfServices.postValue(null)
            }
        }
    }

}