package com.raj.slotify.viewModels.frontend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    // ICONS
    private val _newIcon = MutableLiveData<Int>()
    val newIcon: LiveData<Int> = _newIcon

    // TITLES
    private val _newTitle = MutableLiveData<Int>()
    val newTitle: LiveData<Int> = _newTitle

    private val _secondTitle = MutableLiveData<String>()
    val secondTitle: LiveData<String> = _secondTitle

    // SUBTITLES
    private val _newSubtitle = MutableLiveData<Int>()
    val newSubtitle: LiveData<Int> = _newSubtitle

    // EXPLICATION
    private val _newExplication = MutableLiveData<Int>()
    val newExplication: LiveData<Int> = _newExplication

    // ACTUAL USER TYPE
    private val _userType = MutableLiveData<String>()
    val userType: LiveData<String> = _userType

    // LANGUAGE
    private val _language = MutableLiveData<String>()
    val language: LiveData<String> = _language

    // SET METHODS
    fun setNewTitle(newTitle: Int) {
        _newTitle.postValue(newTitle)
    }

    fun setSecondTitle(secondTitle: String) {
        _secondTitle.postValue(secondTitle)
    }

    fun setNewSubtitle(newSubtitle: Int) {
        _newSubtitle.postValue(newSubtitle)
    }

    fun setNewExplication(newExplication: Int) {
        _newExplication.postValue(newExplication)
    }

    fun setNewIcon(icon: Int) {
        _newIcon.postValue(icon)
    }

    fun setUserType(userType: String) {
        _userType.postValue(userType)
    }

    fun setLanguage(language: String) {
        _language.postValue(language)
    }

}