package com.raj.slotify.tools

import android.content.Context
import android.util.Log
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.raj.slotify.R
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

object Verifier {
    fun <T> verifySuccessfulResponse(
        response: Response<T>,
        context: Context,
        positiveAction: (() -> Unit)?=null,
        positiveActionText: String?=null,
        negativeAction: (() -> Unit)?=null,
        negativeActionText: String?=null
    ): Boolean {

        if (!response.isSuccessful) {
            Log.e(
                "ERROR",
                "[${response.raw().request.url}] ${response.code()}: ${response.message()}"
            )

            if (response.code() == 401) {
                MessageAlerts.showSessionExpiredDialog(context)
                return false
            }

            val rawErrorJson = response.errorBody()?.string()

            Log.e("ERROR_JSON", "JSON recibido: $rawErrorJson")

            var title = context.getString(R.string.error_title)
            var message: String = response.message()

            if (rawErrorJson.isNullOrEmpty()) {
                message = response.message()
            } else {
                var titleJson: String
                var messageJson: String

                try {
                    val json = JSONObject(rawErrorJson)

                    titleJson = json.getString("error")
                    messageJson = json.getString("message")
                } catch (e: JSONException) {
                    titleJson = response.message()
                    messageJson = rawErrorJson
                }

                if (titleJson.isNotEmpty()) {
                    title = titleJson
                }
                if (messageJson.isNotEmpty()) {
                    message = messageJson
                }
            }

            MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveActionText) { dialog, _ ->
                    positiveAction?.invoke()
                }
                .setNegativeButton(negativeActionText) { dialog, _ ->
                    negativeAction?.invoke()
                }.show()

            return false
        }

        if (response.code() != 204 && response.body() == null) {
            Log.e("ERROR", "Response body es null")
            return false
        }

        return true
    }
}