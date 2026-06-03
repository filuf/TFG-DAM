package com.raj.slotify.tools

import android.content.Context
import android.content.Intent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.raj.slotify.R
import com.raj.slotify.activities.LogInActivity

object MessageAlerts {
    fun showSessionExpiredDialog(context: Context) {
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.sesion_expired_title))
            .setMessage(context.getString(R.string.sesion_expired_explication))
            .setPositiveButton(context.getString(R.string.dialog_ok)) { _, _ ->
                val intent = Intent(context, LogInActivity::class.java)
                context.startActivity(intent)
            }.show()
    }

    fun showGenericErrorMessage(title: String, message: String, context: Context) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.dialog_ok, null)
            .show()
    }

    fun showCancelAppointmentButton(context: Context, onCancel: (() -> Unit)?=null, onConfirm: (() -> Unit)?=null) {
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.reserve_cancelation_title))
            .setMessage(context.getString(R.string.reserve_cancelation_confirmation))
            .setNegativeButton(context.getString(R.string.negation_word)) { dialog, which ->
                onCancel?.invoke()
            }
            .setPositiveButton(context.getString(R.string.yes_word)) { dialog, which ->
                onConfirm?.invoke()
            }.show()
    }
}