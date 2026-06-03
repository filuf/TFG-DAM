package com.raj.slotify.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.raj.slotify.R
import com.raj.slotify.viewModels.room.TokenViewModel
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.TokenResponse
import net.openid.appauth.connectivity.ConnectionBuilder
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

class LogInActivity : AppCompatActivity() {

    private lateinit var authService: AuthorizationService
    private val tokenViewModel: TokenViewModel by viewModels()

    // 1. Definimos una configuración que permita HTTP (esto es lo que evita el Crash)
    private val appAuthConfiguration = AppAuthConfiguration.Builder()
        .setConnectionBuilder(HttpConnectionBuilder)
        .setSkipIssuerHttpsCheck(true)
        .build()

    private fun goToHomePage(token: String) {
        Log.i("AUTH", "Intentando abrir HomePage, token: $token")
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("AUTH_TOKEN_ENTITY", token)
        startActivity(intent)
        finish()
    }

    private fun goToFirstFragment() {
        Log.i("AUTH", "Intentando abrir FirstFragment")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun completeLogIn(tokenResponse: TokenResponse?) {
        if (tokenResponse != null) {
            Log.i("AUTH", "Token obtenido correctamente: ${tokenResponse.accessToken!!}")
            Log.i("AUTH", "id token: ${tokenResponse.idToken?:"no id token"}")

            goToHomePage(tokenResponse.jsonSerializeString())

        } else {
            Log.e("AUTH", "No se ha podido obtener el token")
            goToFirstFragment()
        }
    }

    /**
     * Esto cambia el código por un token temporal
     */
    private fun exchangeCodeForToken(response: AuthorizationResponse) {
        authService.performTokenRequest(
            response.createTokenExchangeRequest()
        ) { tokenResponse, exception ->
            if (tokenResponse != null) {
                val accessToken = tokenResponse.accessToken
                if (accessToken != null) {
                    try {
                        // 1. Dividir el token en sus 3 partes
                        val parts = accessToken.split(".")
                        if (parts.size == 3) {
                            // 2. Decodificar el Payload (Base64)
                            val payloadB64 = parts[1]
                            val decodedBytes = Base64.decode(payloadB64, Base64.URL_SAFE)
                            val payloadString = String(decodedBytes, Charset.defaultCharset())

                            // 3. Convertir a JSON y extraer el campo
                            val jsonObject = JSONObject(payloadString)
                            val accountType = jsonObject.optString("account-type", "No definido")

                            Log.i("AUTH", "Tipo de cuenta: $accountType")
                            Toast.makeText(this, "Eres un: $accountType", Toast.LENGTH_SHORT).show()
                            Log.i("AUTH", "Token: $accessToken")

                            completeLogIn(tokenResponse)
                        }
                    } catch (e: Exception) {
                        Log.e("AUTH", "Error al decodificar el token: ${e.toString()}")
                        e.printStackTrace()
                    }
                }
            } else {
                Log.e("AUTH", "Fallo al obtener el token: ${exception?.message}")
                exception?.printStackTrace()
            }
        }
    }

    private fun handleAuthError(error: AuthorizationException) {
        Log.e("AUTH", "Error de AppAuth: [${error.code}] ${error.errorDescription}")

        if (error.code == AuthorizationException.GeneralErrors.USER_CANCELED_AUTH_FLOW.code) {
            Log.w("AUTH", "El usuario cerró el navegador manualmente")

            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.canceled_sesion_title))
                .setMessage(getString(R.string.canceled_sesion_explication))
                .setPositiveButton(getString(R.string.dialog_retry)) { _, _ ->
                    doLogin()
                }.setNegativeButton(getString(R.string.cancel)) { _, _ ->
                    goToFirstFragment()
                }.show()
        } else {
            Toast.makeText(this, "Error: ${error.errorDescription}", Toast.LENGTH_LONG).show()
        }
    }

    private val getAuthResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->val data = result.data

        if (result.resultCode == RESULT_OK && data != null) {
            val response = AuthorizationResponse.fromIntent(data)
            val error = AuthorizationException.fromIntent(data)

            if (response != null) {
                exchangeCodeForToken(response)
            } else if (error != null) {
                handleAuthError(error)
            }
        }

        else if (result.resultCode == RESULT_CANCELED) {
            if (data != null) {
                val error = AuthorizationException.fromIntent(data)
                if (error != null) handleAuthError(error)
            } else {
                Log.e("AUTH", "Ha ocurrido alfgo raro")
            }
        }
    }

    private fun doLogin() {
        /*
        Los primeros pasos son:
        1.- Configurar endpoints
        2.- Crear request
        3.- Lanzar el navegador
         */
        //Para saber de dónde salen las URLS, debemos dirigirnos a http://auth.127.0.0.1.nip.io/realms/master/.well-known/openid-configuration
        //Esto sirve para configurar los puertos de conexión (como el propio nombre indica)
        val serviceConfig = AuthorizationServiceConfiguration(
            "http://auth.10.0.2.2.nip.io/realms/slotify/protocol/openid-connect/auth".toUri(),
            "http://auth.10.0.2.2.nip.io/realms/slotify/protocol/openid-connect/token".toUri()
        )

        //Creamos la petición
        val authRequest = AuthorizationRequest.Builder(
            serviceConfig,
            "android-app-client",
            ResponseTypeValues.CODE,
            "com.raj.slotify://oauth2redirect".toUri()
        )
        .setScope("openid profile email")
        .build()

        val authIntent = authService.getAuthorizationRequestIntent(authRequest)
        getAuthResult.launch(authIntent)

        Toast.makeText(this, "Abriendo Keycloak...", Toast.LENGTH_SHORT).show()
    }

    object HttpConnectionBuilder : ConnectionBuilder {
        override fun openConnection(uri: Uri): HttpURLConnection {
            val conn = URL(uri.toString()).openConnection() as HttpURLConnection
            conn.setRequestProperty("Host", "auth.127.0.0.1.nip.io")
            conn.connectTimeout = 15000
            conn.readTimeout = 10000
            conn.instanceFollowRedirects = false
            return conn
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        authService = AuthorizationService(this, appAuthConfiguration)

        doLogin()
    }

}