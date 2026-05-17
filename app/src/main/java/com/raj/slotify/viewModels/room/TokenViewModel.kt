package com.raj.slotify.viewModels.room

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raj.slotify.room.TokenApp
import com.raj.slotify.room.database.TokenEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TokenViewModel: ViewModel() {

    var database = TokenApp.database

    private val _token = MutableLiveData<TokenEntity?>()
    val token: MutableLiveData<TokenEntity?> = _token

    private val _tokens = MutableLiveData<List<TokenEntity?>>()
    val tokens: MutableLiveData<List<TokenEntity?>> = _tokens

    fun insertToken(token: TokenEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                database.tokenDao().insertToken(token)
                Log.i("INSERTED TOKEN", "Token insertado correctamente: ${token}")
            } catch (e: Exception) {
                Log.e("Error en TokenViewModel", "Error al insertar token: ${e.message}")
            }
        }
    }

    fun getAllTokens() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _tokens.postValue(database.tokenDao().getAllTokens())
            } catch (e: Exception) {
                Log.e("Error en TokenViewModel", "Error al obtener todos los token: ${e.message}")
                _tokens.postValue(emptyList())
            }
        }
    }

    fun getTokenById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _token.postValue(database.tokenDao().getTokenById(id))
            } catch (e: Exception) {
                Log.e("Error en TokenViewModel", "Error al obtener token por ID ${id}: ${e.message}")
                _token.postValue(null)
            }
        }
    }

    fun getLastToken() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _token.postValue(database.tokenDao().getLastToken())
            } catch (e: Exception) {
                Log.e("Error en TokenViewModel", "Error al obtener el último token: ${e.message}")
                token.postValue(null)
            }
        }
    }

    fun deleteTokenById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                database.tokenDao().deleteTokenById(id)
            } catch (e: Exception) {
                Log.e("Error en TokenViewModel", "Error al eliminar token por ID ${id}: ${e.message}")
            }
        }
    }

    fun deleteAllTokens() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                database.tokenDao().deleteAllTokens()
            } catch (e: Exception) {
                Log.e("Error en TokenViewModel", "Error al eliminar todos los tokens: ${e.message}")
            }
        }
    }

}