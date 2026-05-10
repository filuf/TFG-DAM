package com.raj.slotify.services

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import androidx.core.net.toUri
import net.openid.appauth.connectivity.ConnectionBuilder
import java.net.HttpURLConnection
import java.net.URL
import android.net.Uri
import org.json.JSONObject
import java.nio.charset.Charset
import android.util.Base64
import com.raj.slotify.R

class MainActivity1 : AppCompatActivity() {

    private lateinit var authService: AuthorizationService

    // 1. Definimos una configuración que permita HTTP (esto es lo que evita el Crash)
    private val appAuthConfiguration = net.openid.appauth.AppAuthConfiguration.Builder()
        .setConnectionBuilder(HttpConnectionBuilder)
        .setSkipIssuerHttpsCheck(true)
        .build()

    private val getAuthResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val response = AuthorizationResponse.fromIntent(data!!)
            val error = AuthorizationException.fromIntent(data)

            if(response != null) {
                exchangeCodeForToken(response)
            } else {
                Log.e("AUTH", "Error en el login: ${error?.message}")
            }
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
                        }
                    } catch (e: Exception) {
                        Log.e("AUTH", "Error al decodificar el token: ${e.message}")
                    }
                }
            } else {
                Log.e("AUTH", "Fallo al obtener el token: ${exception?.message}")
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main1)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main2)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        authService = AuthorizationService(this, appAuthConfiguration)

        val btnLogin = findViewById<Button>(R.id.btn_login)

        btnLogin.setOnClickListener {
            doLogin()
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
        ).build()

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
}