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

    val service = RetrofitInstance.getService(RegisterUserService::class.java)

    private val _clientRegistered = MutableLiveData<Response<RegisterUserResponse>?>()
    var clientRegistered: LiveData<Response<RegisterUserResponse>?> = _clientRegistered

    private val _companyRegistered = MutableLiveData<Response<RegisterCompanyResponse>?>()
    var companyRegistered: LiveData<Response<RegisterCompanyResponse>?> = _companyRegistered

    fun registerClient(registerUserRequest: RegisterUserRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.w("RegisterUserViewModel", "Intentando registrar cliente")
            val response = service.registerClient(registerUserRequest)
            Log.w("${response.code()}", "Petición de registro hecha: ${response.body()}")
            if (!response.isSuccessful) {
                Log.e(
                    "Error en RegisterUserViewModel",
                    "Error intentando registrar cliente: ${response.code()}: ${response.body()}"
                )
            }
            _clientRegistered.postValue(response)
        }
    }

    fun registerCompany(registerCompanyRequest: RegisterCompanyRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.w("RegisterUserViewModel", "Intentando registrar empresa")
            val response = service.registerCompany(registerCompanyRequest)
            Log.w("${response.code()}", "Petición de registro hecha: ${response.body()}")
            if (!response.isSuccessful) {
                Log.e(
                    "Error en RegisterUserViewModel",
                    "Error intentando registrar empresa: ${response.code()}: ${response.body()}"
                )
            }
            _companyRegistered.postValue(response)
        }
    }

}