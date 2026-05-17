package com.raj.slotify.activities

import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment
import com.raj.slotify.databinding.ActivityMainBinding
import com.raj.slotify.viewModels.apiRest.ReservesViewModel
import com.raj.slotify.viewModels.frontend.MainViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel
import com.raj.slotify.viewModels.room.TokenViewModel
import org.json.JSONObject
import java.nio.charset.Charset
import java.util.UUID
import kotlin.text.split

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val userDataViewModel: UserDataViewModel by viewModels()
    private val tokenViewModel: TokenViewModel by viewModels()
    private val reservesViewModel: ReservesViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }

        // FIND ACCESS TOKEN AND REDIRECT INTO HOME PAGE
        val navHostFragment = supportFragmentManager
            .findFragmentById(com.raj.slotify.R.id.fragmentContainerView3) as NavHostFragment
        val navController = navHostFragment.navController

        val navInflater = navController.navInflater
        val graph = navInflater.inflate(com.raj.slotify.R.navigation.nav_graph_main)

        val accessToken = intent.getStringExtra("accessToken")

        if (!accessToken.isNullOrEmpty()) {
            Log.i("MainActivity", "Token recibido por Intent")
            extractAccessTokenComponents(accessToken)
            graph.setStartDestination(com.raj.slotify.R.id.mainFragment)
            navController.graph = graph

        } else {
            Log.w("MainActivity", "No se ha recibido ningún token, intentando cogerlo de Room")
            tokenViewModel.getLastToken()
            tokenViewModel.token.observe(this) { token ->
                if (token != null) {
                    Log.i("MainActivity", "Token obtenido de Room: ${token.accessToken}")
                    extractAccessTokenComponents(token.accessToken)
                    graph.setStartDestination(com.raj.slotify.R.id.mainFragment)
                } else {
                    graph.setStartDestination(com.raj.slotify.R.id.firstFragment)
                }
                navController.graph = graph
            }
        }
    }

    fun extractAccessTokenComponents(accessToken: String) {
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
                userDataViewModel.setEmail(email)
                userDataViewModel.setName(username)
                userDataViewModel.setAccessToken(accessToken)
                userDataViewModel.setUuid(UUID.fromString(sub))

                reservesViewModel.getReserves("Bearer $accessToken", null, null, null, null)
                reservesViewModel.reservesSummary.observe(this) { response ->
                    if (response != null) {
                        if (response.isSuccessful) {
                            val reserves = response.body()?.content
                            if (reserves != null) {
                                userDataViewModel.setReserves(reserves.toMutableList())
                            }
                        } else {
                            Log.e("ERROR", "Error al obtener reservas: ${response.code()}: ${response.body()}")
                        }
                    }
                }

            }
        } catch (e: Exception) {
            Log.e("ERROR", "Error al decodificar el token: ${e.message}")
        }
    }
}
