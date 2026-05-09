package com.raj.slotify.viewModels.frontend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    // ICONS
    private val _newIcon = MutableLiveData<Int>()
    val newIcon: LiveData<Int> = _newIcon

    private val _oldIcon = MutableLiveData<Int>()
    val oldIcon: LiveData<Int> = _oldIcon

    // TITLES
    private val _newTitle = MutableLiveData<Int>()
    val newTitle: LiveData<Int> = _newTitle

    private val _oldTitle = MutableLiveData<Int>()
    val oldTitle: LiveData<Int> = _oldTitle

    // SUBTITLES
    private val _newSubtitle = MutableLiveData<Int>()
    val newSubtitle: LiveData<Int> = _newSubtitle

    private val _oldSubtitle = MutableLiveData<Int>()
    val oldSubtitle: LiveData<Int> = _oldSubtitle

    // EXPLICATION
    private val _newExplication = MutableLiveData<Int>()
    val newExplication: LiveData<Int> = _newExplication

    private val _oldExplication = MutableLiveData<Int>()
    val oldExplication: LiveData<Int> = _oldExplication

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
    fun setOldTitle(oldTitle: Int) {
        _oldTitle.postValue(oldTitle)
    }

    fun setNewSubtitle(newSubtitle: Int) {
        _newSubtitle.postValue(newSubtitle)
    }
    fun setOldSubtitle(oldSubtitle: Int) {
        _oldSubtitle.postValue(oldSubtitle)
    }

    fun setNewExplication(newExplication: Int) {
        _newExplication.postValue(newExplication)
    }
    fun setOldExplication(oldExplication: Int) {
        _oldExplication.postValue(oldExplication)
    }

    fun setNewIcon(icon: Int) {
        _newIcon.postValue(icon)
    }

    fun setOldIcon(icon: Int) {
        _oldIcon.postValue(icon)
    }

    fun setUserType(userType: String) {
        _userType.postValue(userType)
    }

    fun setLanguage(language: String) {
        _language.postValue(language)
    }



}