package com.raj.slotify.tools

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

object Formater {

    fun Any.toRequestBody(): RequestBody {
        return Gson().toJson(this).toRequestBody("application/json".toMediaTypeOrNull())
    }

    fun Uri.uriToFile(context: Context): File? {
        val contentResolver = context.contentResolver

        // 1. Detectar la extensión real del archivo (jpg, png, webp...)
        val mimeTypeMap = MimeTypeMap.getSingleton()
        val extension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(this)) ?: "jpg"

        val inputStream = contentResolver.openInputStream(this) ?: return null

        // 2. Crear el archivo temporal con la extensión correcta
        val tempFile = File.createTempFile("upload_image", ".$extension", context.cacheDir)

        tempFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        return tempFile
    }

    // Función extra para obtener el MediaType exacto al subir
    fun Uri.getMediaType(context: Context): MediaType? {
        return context.contentResolver.getType(this)?.toMediaTypeOrNull()
    }

}