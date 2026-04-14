package com.example.tfgapplication.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgapplication.models.SongResponse
import com.example.tfgapplication.services.APIService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CancionesViewModel: ViewModel() {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080") //IP que conecta con el localhost del anfitrión (tu pc)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(APIService::class.java)

    private val _songs = MutableLiveData<List<SongResponse?>>()
    val songs: LiveData<List<SongResponse?>> = _songs

    fun getSongs() {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                service.getSongs()
            }
            if (response.isSuccessful) {
                _songs.postValue(response.body())
            }else{
                Log.i("Error en la carga","Petición fallida. ${response.code()}")
                _songs.postValue(mutableListOf(null))
            }
        }
    }

    fun getSongByCode(code: String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                service.getSongByID(code)
            }
            if (response.isSuccessful) {
                _songs.postValue(mutableListOf(response.body()))
            }else{
                Log.i("error en la carga","Petición fallida. ${response.code()}")
                _songs.postValue(mutableListOf(null))
            }
        }
    }

    fun postSong(song: SongResponse) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                service.postSong(song)
            }
            if (response.isSuccessful) {
                _songs.postValue(mutableListOf(response.body()))
            }else{
                Log.i("error en la carga","Petición fallida. ${response.code()}")
                _songs.postValue(mutableListOf(null))
            }
        }
    }

    fun updateSong(song: SongResponse) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                service.updateCancion(song)
            }
            if (response.isSuccessful) {
                _songs.postValue(mutableListOf(response.body()))
            }else{
                Log.i("error en la carga","Petición fallida. ${response.code()}")
                _songs.postValue(mutableListOf(null))
            }
        }
    }

    fun deleteSongByCode(code: String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                service.deleteCancionByCodigo(code)
            }
            if (response.isSuccessful) {
                _songs.postValue(mutableListOf(response.body()))
            }else{
                Log.i("error en la carga","Petición fallida. ${response.code()}")
                _songs.postValue(mutableListOf(null))
            }
        }
    }
}