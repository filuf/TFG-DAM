package com.raj.slotify.viewModels.frontend

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.raj.slotify.dtos.reserves.ReserveSummary
import com.raj.slotify.models.ReserveEntity
import java.util.UUID

class UserDataViewModel: ViewModel() {

    private val _accessToken = MutableLiveData<String>()
    private val _uuid = MutableLiveData<UUID>()
    private val _name = MutableLiveData<String>()
    private val _lastName = MutableLiveData<String>()
    private val _email = MutableLiveData<String>()
    private val _phone = MutableLiveData<String>()
    private val _password = MutableLiveData<String>()
    private val _imageUri = MutableLiveData<Uri>()
    private val _userType = MutableLiveData<String>()
    private val _reserves = MutableLiveData<MutableList<ReserveSummary>>()

    val accessToken: LiveData<String> = _accessToken
    val uuid: LiveData<UUID> = _uuid
    val name: LiveData<String> = _name
    val lastName: LiveData<String> = _lastName
    val email: LiveData<String> = _email
    val phone: LiveData<String> = _phone
    val password: LiveData<String> = _password
    val imageUri: LiveData<Uri> = _imageUri
    val userType: LiveData<String> = _userType
    val reserves: LiveData<MutableList<ReserveSummary>> = _reserves

    fun setAccessToken(accessToken: String) {
        _accessToken.postValue(accessToken)
    }

    fun setUuid(uuid: UUID) {
        _uuid.postValue(uuid)
    }

    fun setName(name: String) {
        _name.postValue(name)
    }

    fun setEmail(email: String) {
        _email.postValue(email)
    }

    fun setPhone(phone: String) {
        _phone.postValue(phone)
    }

    fun setPassword(password: String) {
        _password.postValue(password)
    }

    fun setImageUri(imageUri: Uri) {
        _imageUri.postValue(imageUri)
    }

    fun setUserType(userType: String) {
        _userType.postValue(userType)
    }

    fun setReserves(reserves: MutableList<ReserveSummary>) {
        _reserves.postValue(reserves)
    }

}