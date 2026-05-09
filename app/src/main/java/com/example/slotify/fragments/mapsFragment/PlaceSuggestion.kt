package com.example.slotify.fragments.mapsFragment

import com.google.gson.annotations.SerializedName

data class PlaceSuggestion(
    @SerializedName("display_name") val displayName: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double
)
