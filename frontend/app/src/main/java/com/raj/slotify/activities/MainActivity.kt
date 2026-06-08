package com.raj.slotify.activities

import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.navigation.fragment.NavHostFragment
import com.raj.slotify.R
import com.raj.slotify.databinding.ActivityMainBinding
import com.raj.slotify.room.database.TokenEntity
import com.raj.slotify.tools.Verifier
import com.raj.slotify.viewModels.apiRest.CompanyViewModel
import com.raj.slotify.viewModels.apiRest.ReservesViewModel
import com.raj.slotify.viewModels.apiRest.UserViewModel
import com.raj.slotify.viewModels.frontend.ClientReservesViewModel
import com.raj.slotify.viewModels.frontend.MainViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel
import com.raj.slotify.viewModels.room.TokenViewModel
import net.openid.appauth.TokenResponse
import org.json.JSONObject
import java.nio.charset.Charset
import java.util.UUID
import kotlin.text.split

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val userDataViewModel: UserDataViewModel by viewModels()
    private val clientReservesViewModel: ClientReservesViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val companyViewModel: CompanyViewModel by viewModels()
    private val tokenViewModel: TokenViewModel by viewModels()
    private val reservesViewModel: ReservesViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // FIND ACCESS TOKEN AND REDIRECT INTO HOME PAGE
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView3) as NavHostFragment
        val navController = navHostFragment.navController

        val navInflater = navController.navInflater
        val graph = navInflater.inflate(R.navigation.nav_graph_main)

        val authToken = intent.getStringExtra("AUTH_TOKEN_ENTITY")

        if (!authToken.isNullOrEmpty()) {
            Log.i("MainActivity", "Token recibido por Intent")

            val tokenResponse = TokenResponse.jsonDeserialize(authToken)
            val accessToken = tokenResponse.accessToken

            if (accessToken != null) {
                extractTokenComponents(accessToken)
                val tokenEntity = TokenEntity(
                    0,
                    accessToken,
                    tokenResponse.refreshToken?:"null",
                    tokenResponse.tokenType?:"null",
                    tokenResponse.accessTokenExpirationTime?:0,
                    tokenResponse.idToken?:"null",
                    tokenResponse.scope?:"null"
                )
                uploadToken(tokenEntity)
                tokenViewModel.insertToken(tokenEntity)

                Log.i("MainActivity", "Token obtenido por Intent: $accessToken")

                graph.setStartDestination(R.id.mainFragment)
                navController.graph = graph
            } else {
                Log.e("MainActivity", "No se ha recibido el access token en el Intent")
            }

        } else {
            Log.w("MainActivity", "No se ha recibido ningún token, intentando cogerlo de Room")
            tokenViewModel.getLastToken()
            tokenViewModel.token.observe(this) { token ->
                if (token != null && System.currentTimeMillis() < token.expiresIn) {
                    Log.i("MainActivity", "Token obtenido de Room: ${token.accessToken}")
                    extractTokenComponents(token.accessToken)
                    uploadToken(token)
                    graph.setStartDestination(R.id.mainFragment)
                } else {
                    Log.w("MainActivity", "Token de Room expirado, redirigiendo a login")
                    graph.setStartDestination(R.id.firstFragment)
                }
                navController.graph = graph
            }
        }
    }

    fun uploadToken(tokenEntity: TokenEntity) {
        userDataViewModel.setUserToken(tokenEntity)
    }

    fun extractTokenComponents(accessToken: String) {
        try {
            val parts = accessToken.split(".")
            if (parts.size == 3) {
                val payloadB64 = parts[1]
                val decodedBytes = Base64.decode(payloadB64, Base64.URL_SAFE)
                val payloadString = String(decodedBytes, Charset.defaultCharset())

                val jsonObject = JSONObject(payloadString)

                val sub = jsonObject.optString("sub")

                val username = jsonObject.optString("preferred_username")
                val email = jsonObject.optString("email")

                val accountType = jsonObject.optString("account-type")

                Log.d("JWT_DECODE", "Account Type: $accountType")
                Log.d("JWT_DECODE", "User ID (sub): $sub")
                Log.d("JWT_DECODE", "Username: $username")
                Log.d("JWT_DECODE", "Email: $email")

                userDataViewModel.setUserType(accountType)
                userDataViewModel.setName(username)
                userDataViewModel.setUuid(UUID.fromString(sub))
                userDataViewModel.setEmail(email)

                // UPDATE USER DATA
                if (accountType == "USER") {
                    userViewModel.getUser(UUID.fromString(sub), "Bearer $accessToken")
                    userViewModel.userSearched.observe(this) { response ->
                        if (response == null) {
                            return@observe
                        }
                        if (!Verifier.verifySuccessfulResponse(response, this, positiveActionText = getString(R.string.dialog_ok))) {
                            return@observe
                        }
                        val user = response.body()
                        userDataViewModel.setName(user?.username?:username)

                        if (!user?.s3ImageUrl.isNullOrEmpty())
                            userDataViewModel.setImageUri(user.s3ImageUrl.toUri())
                    }
                } else {
                    companyViewModel.getCompanyById(UUID.fromString(sub), "Bearer $accessToken")
                    companyViewModel.company.observe(this) { response ->
                        if (response == null) {
                            return@observe
                        }
                        if (!Verifier.verifySuccessfulResponse(response, this, positiveActionText = getString(R.string.dialog_ok))) {
                            return@observe
                        }
                        val company = response.body()
                        userDataViewModel.setName(company?.companyName?:username)

                        Log.w("MainActivivy", "company: $company")

                        if (!company?.s3ImageUrl.isNullOrEmpty())
                            userDataViewModel.setImageUri(company.s3ImageUrl.toUri())
                        else
                            Log.w("MainActivity", "No se ha recibido la imagen de la empresa")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Error al decodificar el token: ${e.message}")
        }
    }
}
