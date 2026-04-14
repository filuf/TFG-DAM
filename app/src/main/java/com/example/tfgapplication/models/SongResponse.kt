package com.example.tfgapplication.models

import com.google.gson.annotations.SerializedName

data class SongResponse (
    val id: String,
    val codigo:String,
    @SerializedName("titulo") // Valor original del campo en el JSON de mis objeto canción en mi api rest
    val título: String, // Valor custom
    val artistas: List<String>,
    val duracion: String,
    val urlPortada: String,
    val esFavorita: Boolean
)
