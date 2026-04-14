package com.example.tfgapplication

import com.example.tfgapplication.models.SongResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder

// Clase sencilla que te serializa o deserializa un objeto de Kotlin
object Transformer {

    fun serializeSongObject (song: SongResponse?): String {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val json = gson.toJson(song)
        return json
    }

    fun deserializeSongObject (json: String): SongResponse {
        val gson = Gson()
        val song = gson.fromJson(json, SongResponse::class.java)
        return song
    }

}