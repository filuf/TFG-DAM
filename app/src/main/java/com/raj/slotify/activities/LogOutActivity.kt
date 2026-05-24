package com.raj.slotify.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.raj.slotify.R
import com.raj.slotify.viewModels.room.TokenViewModel
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.EndSessionRequest
import net.openid.appauth.EndSessionResponse
import net.openid.appauth.connectivity.ConnectionBuilder
import java.net.HttpURLConnection
import java.net.URL

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

    private val logoutLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // ESTE BLOQUE se ejecuta cuando el navegador se cierra y vuelve a tu app
        Log.i("LogOutActivity", "El navegador ha vuelto. Borrando tokens...")

        // Ahora sí, borramos los datos de la base de datos local
        tokenViewModel.deleteAllTokens()

        // Navegamos a la pantalla principal (limpia)
        val intentToMainActivity = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intentToMainActivity)
        finish()
    }

    fun logout(idToken: String) {
        try {
            Log.i("Log out activity", "Iniciando proceso de Logout")

            // 1. IMPORTANTE: Usamos la URL que configuramos para Keycloak
            val keycloakUrl = "http://auth.10.0.2.2.nip.io"
            val serviceConfig = AuthorizationServiceConfiguration(
                "http://auth.10.0.2.2.nip.io/realms/slotify/protocol/openid-connect/auth".toUri(),
                "http://auth.10.0.2.2.nip.io/realms/slotify/protocol/openid-connect/token".toUri(),
                null,
                "http://auth.10.0.2.2.nip.io/realms/slotify/protocol/openid-connect/logout".toUri()
            )

            val endSessionRequest = EndSessionRequest.Builder(serviceConfig)
                .setIdTokenHint(idToken)
                // Esta URI debe estar en "Valid Post Logout Redirect URIs" en Keycloak
                .setPostLogoutRedirectUri("com.raj.slotify://oauth2redirect".toUri())
                .build()

            val logoutIntent = authService.getEndSessionRequestIntent(endSessionRequest)

            Log.i("Log out activity", "Lanzando navegador para logout...")

            // 3. SOLO ESTA LÍNEA. El resto del trabajo se hace en el callback del 'logoutLauncher'
            logoutLauncher.launch(logoutIntent)

        } catch (e: Exception) {
            Log.e("LogOutActivity", "Error al cerrar sesión: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        authService.dispose()
    }
}