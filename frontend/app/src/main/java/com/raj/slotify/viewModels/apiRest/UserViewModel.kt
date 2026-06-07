package com.raj.slotify.viewModels.apiRest

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raj.slotify.dtos.user.UserSummary
import com.raj.slotify.services.retrofit.RetrofitInstance
import com.raj.slotify.services.retrofit.slotifyBackend.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.util.UUID

class UserViewModel: ViewModel() {

    private val service = RetrofitInstance.getService(UserService::class.java)

    private val _userSearched = MutableLiveData<Response<UserSummary>?>()
    val userSearched: LiveData<Response<UserSummary>?> = _userSearched

    private val _userUpdated = MutableLiveData<Response<UserSummary>?>()
    val userUpdated: LiveData<Response<UserSummary>?> = _userUpdated

    fun getUser(userId: UUID, authHeader: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = service.getUser(userId, authHeader)
                if (!response.isSuccessful) {
                    Log.e("UserViewModel", "Error obteniendo usuario: ${response.code()}: ${response.message()}")
                }
                _userSearched.postValue(response)
            } catch (e: Exception) {
                Log.e("UserViewModel", "Excepción al obtener usuario: ${e.message}")
                _userSearched.postValue(null)
            }
        }
    }

    fun patchUser(authHeader: String, file: MultipartBody.Part?, request: RequestBody) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = service.patchUser(authHeader, file, request)
                if (!response.isSuccessful) {
                    Log.e("UserViewModel", "Error actualizando usuario: ${response.code()}: ${response.message()}")
                }
                _userUpdated.postValue(response)
            } catch (e: Exception) {
                Log.e("UserViewModel", "Excepción al actualizar usuario: ${e.message}")
                _userUpdated.postValue(null)
            }
        }
    }

}