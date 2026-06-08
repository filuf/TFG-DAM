package com.raj.slotify.viewModels.frontend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.raj.slotify.models.TextModel

class MainViewModel: ViewModel() {

    // ICONS
    private val _icon = MutableLiveData<Int>()
    val icon: LiveData<Int> = _icon

    // TITLES
    private val _title = MutableLiveData<TextModel>()
    val title: LiveData<TextModel> = _title

    private val _secondTitle = MutableLiveData<String>()
    val secondTitle: LiveData<String> = _secondTitle

    // SUBTITLES
    private val _subtitle = MutableLiveData<TextModel>()
    val subtitle: LiveData<TextModel> = _subtitle

    // EXPLICATION
    private val _explication = MutableLiveData<TextModel>()
    val explication: LiveData<TextModel> = _explication

    // ACTUAL USER TYPE
    private val _userType = MutableLiveData<String>()
    val userType: LiveData<String> = _userType

    // LANGUAGE
    private val _language = MutableLiveData<String>()
    val language: LiveData<String> = _language

    // SET METHODS
    fun setTitle(newTitle: TextModel) {
        _title.postValue(newTitle)
    }

    fun setSecondTitle(secondTitle: String) {
        _secondTitle.postValue(secondTitle)
    }

    fun setSubtitle(newSubtitle: TextModel) {
        _subtitle.postValue(newSubtitle)
    }

    fun setExplication(newExplication: TextModel) {
        _explication.postValue(newExplication)
    }

    fun setIcon(icon: Int) {
        _icon.postValue(icon)
    }

    fun setUserType(userType: String) {
        _userType.postValue(userType)
    }

    fun setLanguage(language: String) {
        _language.postValue(language)
    }

}