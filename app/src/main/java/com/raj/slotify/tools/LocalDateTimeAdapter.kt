package com.raj.slotify.tools

import com.google.gson.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.lang.reflect.Type

// 1. Creamos el adaptador para LocalDateTime
class LocalDateTimeAdapter : JsonDeserializer<LocalDateTime>, JsonSerializer<LocalDateTime> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    // De JSON a Kotlin (para recibir datos)
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalDateTime {
        return LocalDateTime.parse(json?.asString, formatter)
    }

    // De Kotlin a JSON (por si envías datos)
    override fun serialize(src: LocalDateTime?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(formatter.format(src))
    }
}