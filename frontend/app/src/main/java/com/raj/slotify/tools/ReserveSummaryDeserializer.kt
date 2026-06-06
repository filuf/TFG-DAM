package com.raj.slotify.tools

import android.util.Log
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.raj.slotify.dtos.reserves.CompanyReserveSummary
import com.raj.slotify.dtos.reserves.ReserveSummary
import com.raj.slotify.dtos.reserves.UserReserveSummary

class ReserveSummaryDeserializer : JsonDeserializer<ReserveSummary> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: java.lang.reflect.Type?,
        context: JsonDeserializationContext?
    ): ReserveSummary? {
        if (json == null || context == null)  {
            Log.e("ReserveSummaryDeserializer", "JSON o context nulos")
            return null
        }

        val jsonObject = json.asJsonObject

        // Si el JSON tiene "companyName", es para un USUARIO
        return if (jsonObject.has("companyName")) {
            context.deserialize(json, UserReserveSummary::class.java)
        } else {
            // Si no, es para una EMPRESA
            context.deserialize(json, CompanyReserveSummary::class.java)
        }
    }
}