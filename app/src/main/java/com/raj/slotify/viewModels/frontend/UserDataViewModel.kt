package com.raj.slotify.viewModels.frontend

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    val accessToken: MutableLiveData<String> = _accessToken
    val uuid: MutableLiveData<UUID> = _uuid
    val name: MutableLiveData<String> = _name
    val lastName: MutableLiveData<String> = _lastName
    val email: MutableLiveData<String> = _email
    val phone: MutableLiveData<String> = _phone
    val password: MutableLiveData<String> = _password
    val imageUri: MutableLiveData<Uri> = _imageUri
    val userType: MutableLiveData<String> = _userType

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

}