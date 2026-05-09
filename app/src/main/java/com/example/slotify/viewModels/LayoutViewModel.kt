package com.example.slotify.viewModels

import android.view.View
import androidx.activity.result.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.transition.Visibility
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
    
    val nextButtonVisibility: LiveData<Int> = _nextButtonVisibility
    val backButtonVisibility: LiveData<Int> = _backButtonVisibility
    val cancelButtonVisibility: LiveData<Int> = _cancelButtonVisibility
    val confirmButtonVisibility: LiveData<Int> = _confirmButtonVisibility
    val descriptionVisibility: LiveData<Int> = _descriptionVisibility
    val explicationVisibility: LiveData<Int> = _explicationVisibility
    val imageVisibility: LiveData<Int> = _imageVisibility
    val superiorFragmentVisibility: LiveData<Int> = _superiorFragmentVisibility
    val inferiorFragmentVisibility: LiveData<Int> = _inferiorFragmentVisibility

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

    private val _nextButtonClicked = MutableSharedFlow<Unit>(replay = 0)
    val nextButtonClicked = _nextButtonClicked.asSharedFlow()

    fun onNextClicked() {
        viewModelScope.launch { _nextButtonClicked.emit(Unit) }
    }

}