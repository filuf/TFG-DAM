package com.raj.slotify.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.raj.slotify.BuildConfig
import com.raj.slotify.R
import com.raj.slotify.room.database.TokenEntity
import com.raj.slotify.viewModels.room.TokenViewModel
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.EndSessionRequest
import net.openid.appauth.TokenResponse

class LogOutActivity : AppCompatActivity() {

    private lateinit var authService: AuthorizationService
    private val tokenViewModel: TokenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        authService = AuthorizationService(this)

        val idToken = intent.getStringExtra("ID_TOKEN")

        if (!idToken.isNullOrBlank()) {
            logout(idToken)
        } else {
            val errorMsg = "idToken está vacío"
            Log.e("LogOutActivity", "Error: $errorMsg")

            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.cant_close_session_title))
                .setMessage(getString(R.string.cant_close_sesion_message))
                .setPositiveButton(getString(R.string.dialog_ok)) { _, _ ->
                    val intentToMainActivity = Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intentToMainActivity)
                    finish()
                }
                .show()
        }
    }

    fun logout(idToken: String) {
        try {
            val authService = AuthorizationService(this)

            val keycloakUrl = "http://auth.10.0.2.2.nip.io"

            val serviceConfig = AuthorizationServiceConfiguration(
                "$keycloakUrl/auth/realms/master/protocol/openid-connect/auth".toUri(),
                "$keycloakUrl/auth/realms/master/protocol/openid-connect/token".toUri(),
                null, // registration endpoint
                "$keycloakUrl/auth/realms/master/protocol/openid-connect/logout".toUri() // <--- ESTO ES LO QUE BUSCAMOS
            )

            val endSessionRequest = EndSessionRequest.Builder(serviceConfig)
                .setIdTokenHint(idToken) // <--- USA EL ID TOKEN DEL RESPONSE
                .setPostLogoutRedirectUri("com.raj.slotify:/logout_callback".toUri())
                .build()

            val logoutIntent = authService.getEndSessionRequestIntent(endSessionRequest)
            val intentToMainActivity = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            startActivity(logoutIntent)
            tokenViewModel.deleteAllTokens()

            startActivity(intentToMainActivity)
            finish()

        } catch (e: Exception) {
            Log.e("LogOutActivity", "Error al cerrar sesión: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        authService.dispose()
    }
}