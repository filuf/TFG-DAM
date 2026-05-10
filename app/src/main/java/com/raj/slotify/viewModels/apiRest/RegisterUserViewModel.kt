package com.raj.slotify.viewModels.apiRest

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.raj.slotify.BuildConfig
import com.raj.slotify.dtos.registry.client.RegisterUserRequest
import com.raj.slotify.dtos.registry.client.RegisterUserResponse
import com.raj.slotify.dtos.registry.company.RegisterCompanyRequest
import com.raj.slotify.dtos.registry.company.RegisterCompanyResponse
import com.raj.slotify.services.retrofit.RetrofitInstance
import com.raj.slotify.services.retrofit.slotifyBackend.RegisterUserService
import retrofit2.Response

class RegisterUserViewModel: ViewModel() {

    val appInDebug: Boolean = BuildConfig.DEBUG
    val baseUrl: String = if (appInDebug) BuildConfig.SPRING_TEST_URL else BuildConfig.SPRING_BASE_URL

    val endpoint: String = baseUrl + "auth/"
    val service = RetrofitInstance.getApiService<RegisterUserService>(endpoint)

    private val _clientRegistered = MutableLiveData<Response<RegisterUserResponse>?>()
    var clientRegistered: LiveData<Response<RegisterUserResponse>?> = _clientRegistered

    private val _companyRegistered = MutableLiveData<Response<RegisterCompanyResponse>?>()
    var companyRegistered: LiveData<Response<RegisterCompanyResponse>?> = _companyRegistered

    fun registerClient(registerUserRequest: RegisterUserRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = service.registerClient(registerUserRequest)
            if (!response.isSuccessful) {
                Log.e(
                    "Error en RegisterUserViewModel",
                    "Error intentando registrar cliente: ${response.code()}: ${response.message()}"
                )
            }
            _clientRegistered.postValue(response)
        }
    }

    fun registerCompany(registerCompanyRequest: RegisterCompanyRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = service.registerCompany(registerCompanyRequest)
            if (!response.isSuccessful) {
                Log.e(
                    "Error en RegisterUserViewModel",
                    "Error intentando registrar empresa: ${response.code()}: ${response.message()}"
                )
            }
            _companyRegistered.postValue(response)
        }
    }

}