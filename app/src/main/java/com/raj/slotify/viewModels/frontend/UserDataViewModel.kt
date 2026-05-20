package com.raj.slotify.viewModels.frontend

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.raj.slotify.dtos.reserves.ReserveSummary
import com.raj.slotify.room.database.TokenEntity
import java.util.UUID

class UserDataViewModel: ViewModel() {


    private val _userToken = MutableLiveData<TokenEntity>()
    private val _uuid = MutableLiveData<UUID>()
    private val _name = MutableLiveData<String>()
    private val _lastName = MutableLiveData<String>()
    private val _email = MutableLiveData<String>()
    private val _phone = MutableLiveData<String>()
    private val _password = MutableLiveData<String>()
    private val _imageUri = MutableLiveData<Uri>()
    private val _userType = MutableLiveData<String>()
    private val _reserves = MutableLiveData<MutableList<ReserveSummary>>()
    private val _lastReserveSelected = MutableLiveData<ReserveSummary>()
    private val _serviceLocation = MutableLiveData<String>()

    val userToken: LiveData<TokenEntity> = _userToken
    val uuid: LiveData<UUID> = _uuid
    val name: LiveData<String> = _name
    val lastName: LiveData<String> = _lastName
    val email: LiveData<String> = _email
    val phone: LiveData<String> = _phone
    val password: LiveData<String> = _password
    val imageUri: LiveData<Uri> = _imageUri
    val userType: LiveData<String> = _userType
    val reserves: LiveData<MutableList<ReserveSummary>> = _reserves
    val lastReserveSelected: LiveData<ReserveSummary> = _lastReserveSelected
    val serviceLocation: LiveData<String> = _serviceLocation

    fun setUserToken(userToken: TokenEntity) {
        _userToken.postValue(userToken)
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

    fun setLastReserveSelected(lastReserveSelected: ReserveSummary) {
        _lastReserveSelected.postValue(lastReserveSelected)
    }

    fun setServiceLocation(serviceLocation: String) {
        _serviceLocation.postValue(serviceLocation)
    }

}