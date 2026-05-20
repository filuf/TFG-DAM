package com.raj.slotify.tools
import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class LocalTimeAdapter : JsonDeserializer<LocalTime>, JsonSerializer<LocalTime> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_TIME

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalTime {
        return LocalTime.parse(json?.asString, formatter)
    }

    override fun serialize(src: LocalTime?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(formatter.format(src))
    }
}