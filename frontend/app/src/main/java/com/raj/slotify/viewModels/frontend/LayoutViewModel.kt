package com.raj.slotify.viewModels.frontend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class LayoutViewModel: ViewModel() {

    private val _backButtonVisibility = MutableLiveData<Int>()
    private val _nextButtonVisibility = MutableLiveData<Int>()
    private val _cancelButtonVisibility = MutableLiveData<Int>()
    private val _confirmButtonVisibility = MutableLiveData<Int>()
    private val _descriptionVisibility = MutableLiveData<Int>()
    private val _explicationVisibility = MutableLiveData<Int>()
    private val _imageVisibility = MutableLiveData<Int>()
    private val _superiorFragmentVisibility = MutableLiveData<Int>()
    private val _inferiorFragmentVisibility = MutableLiveData<Int>()
    private val _secondTextVisibility = MutableLiveData<Int>()
    private val _bottomNavVisibility = MutableLiveData<Int>()
    private val _centerIconTitle = MutableLiveData<Boolean>()
    
    val nextButtonVisibility: LiveData<Int> = _nextButtonVisibility
    val backButtonVisibility: LiveData<Int> = _backButtonVisibility
    val cancelButtonVisibility: LiveData<Int> = _cancelButtonVisibility
    val confirmButtonVisibility: LiveData<Int> = _confirmButtonVisibility
    val descriptionVisibility: LiveData<Int> = _descriptionVisibility
    val explicationVisibility: LiveData<Int> = _explicationVisibility
    val imageVisibility: LiveData<Int> = _imageVisibility
    val superiorFragmentVisibility: LiveData<Int> = _superiorFragmentVisibility
    val inferiorFragmentVisibility: LiveData<Int> = _inferiorFragmentVisibility
    val bottomNavVisibility: LiveData<Int> = _bottomNavVisibility
    val centerIconTitle: LiveData<Boolean> = _centerIconTitle
    val secondTextVisibility: LiveData<Int> = _secondTextVisibility

    // BUTTONS
    private val _nextButtonClicked = MutableSharedFlow<Unit>(replay = 0)
    val nextButtonClicked = _nextButtonClicked.asSharedFlow()
    private val _confirmButtonClicked = MutableSharedFlow<Unit>(replay = 0)
    val confirmButtonClicked = _confirmButtonClicked.asSharedFlow()

    // TOOLBAR
    // En MainViewModel.kt o LayoutViewModel.kt
    private val _toolbarTitle = MutableLiveData<String>()
    val toolbarTitle: LiveData<String> = _toolbarTitle

    private val _toolbarNavigationIcon = MutableLiveData<Int?>()
    val toolbarNavigationIcon: LiveData<Int?> = _toolbarNavigationIcon

    fun setNextButtonVisibility(visibility: Int) {
        _nextButtonVisibility.postValue(visibility)
    }

    fun setBackButtonVisibility(visibility: Int) {
        _backButtonVisibility.postValue(visibility)
    }

    fun setCancelButtonVisibility(visibility: Int) {
        _cancelButtonVisibility.postValue(visibility)
    }

    fun setConfirmButtonVisibility(visibility: Int) {
        _confirmButtonVisibility.postValue(visibility)
    }

    fun setDescriptionVisibility(visibility: Int) {
        _descriptionVisibility.postValue(visibility)
    }

    fun setExplicationVisibility(visibility: Int) {
        _explicationVisibility.postValue(visibility)
    }

    fun setImageVisibility(visibility: Int) {
        _imageVisibility.postValue(visibility)
    }

    fun setSuperiorFragmentVisibility(visibility: Int) {
        _superiorFragmentVisibility.postValue(visibility)
    }

    fun setInferiorFragmentVisibility(visibility: Int) {
        _inferiorFragmentVisibility.postValue(visibility)
    }

    fun setNavBottomVisibility(visibility: Int) {
        _bottomNavVisibility.postValue(visibility)
    }

    fun setCenterIconTitle(centerIconTitle: Boolean) {
        _centerIconTitle.postValue(centerIconTitle)
    }

    fun setSecondTextVisibility(visibility: Int) {
        _secondTextVisibility.postValue(visibility)
    }

    fun onNextClicked() {
        viewModelScope.launch { _nextButtonClicked.emit(Unit) }
    }

    fun onConfirmClicked() {
        viewModelScope.launch { _confirmButtonClicked.emit(Unit) }
    }

    fun updateToolbar(title: String, iconRes: Int?) {
        _toolbarTitle.value = title
        _toolbarNavigationIcon.value = iconRes
    }

}