package com.raj.slotify.tools

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.internal.Streams
import java.io.IOException

class RuntimeTypeAdapterFactory<T> private constructor(
    private val baseType: Class<T>,
    private val typeFieldName: String
) : TypeAdapterFactory {
    private val labelToSubtype = LinkedHashMap<String, Class<out T>>()
    private val subtypeToLabel = LinkedHashMap<Class<out T>, String>()

    override fun <R> create(gson: Gson, type: TypeToken<R>): TypeAdapter<R>? {
        if (type.rawType != baseType) return null

        val labelToDelegate = LinkedHashMap<String, TypeAdapter<out T>>()
        val subtypeToDelegate = LinkedHashMap<Class<out T>, TypeAdapter<out T>>()

        for ((label, subtype) in labelToSubtype) {
            val delegate = gson.getDelegateAdapter(this, TypeToken.get(subtype))
            labelToDelegate[label] = delegate
            subtypeToDelegate[subtype] = delegate
        }

        return object : TypeAdapter<R>() {
            @Throws(IOException::class)
            override fun read(`in`: com.google.gson.stream.JsonReader): R {
                val jsonElement = Streams.parse(`in`)
                val labelElement = jsonElement.asJsonObject.remove(typeFieldName)
                    ?: throw JsonParseException("Missing property '$typeFieldName' for polimorphic type")
                val label = labelElement.asString
                val delegate = labelToDelegate[label] as? TypeAdapter<R>
                    ?: throw JsonParseException("Unknown type label '$label'")
                return delegate.fromJsonTree(jsonElement)
            }

            @Throws(IOException::class)
            override fun write(out: com.google.gson.stream.JsonWriter, value: R) {
                // 1. Obtenemos la clase física del objeto
                val subtype = value!!::class.java

                // 2. Hacemos el cast a Class<out T> para que el mapa lo reconozca
                val label = subtypeToLabel[subtype as Class<out T>]
                    ?: throw JsonParseException("Can't serialize ${subtype.name}; did you forget to register it?")

                val delegate = subtypeToDelegate[subtype] as TypeAdapter<R>
                val jsonObject = delegate.toJsonTree(value).asJsonObject

                if (jsonObject.has(typeFieldName)) {
                    throw JsonParseException("Can't serialize ${subtype.name} because it already contains a field named $typeFieldName")
                }

                val clone = JsonObject()
                clone.addProperty(typeFieldName, label)
                for ((key, element) in jsonObject.entrySet()) {
                    clone.add(key, element)
                }
                Streams.write(clone, out)
            }
        }.nullSafe() as TypeAdapter<R>
    }

    companion object {
        fun <T> of(baseType: Class<T>, typeFieldName: String): RuntimeTypeAdapterFactory<T> {
            return RuntimeTypeAdapterFactory(baseType, typeFieldName)
        }
    }

    fun registerSubtype(subtype: Class<out T>, label: String): RuntimeTypeAdapterFactory<T> {
        labelToSubtype[label] = subtype
        subtypeToLabel[subtype] = label
        return this
    }
}