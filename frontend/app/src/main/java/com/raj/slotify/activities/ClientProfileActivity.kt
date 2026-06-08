package com.raj.slotify.activities

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import com.raj.slotify.R
import com.raj.slotify.databinding.ActivityClientProfileBinding
import com.raj.slotify.tools.TextUtils
import com.raj.slotify.tools.Verifier
import com.raj.slotify.viewModels.apiRest.UserViewModel
import com.raj.slotify.viewModels.room.TokenViewModel
import java.util.UUID

class ClientProfileActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModels()
    private val tokenViewModel: TokenViewModel by viewModels()

    private lateinit var binding: ActivityClientProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityClientProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            binding.materialToolbar.setPadding(0, systemBars.top, 0, 0)
            insets
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        setUpToolbar()
        observeUser()

        val userId = intent.getStringExtra("USER_ID")?.let { UUID.fromString(it) }

        if (userId == null) {
            Log.e("ClientProfileActivity", "No se ha recibido el ID del cliente")
            finish()
            return
        }

        loadUser(userId)
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.materialToolbar)
        binding.materialToolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadUser(userId: UUID) {
        tokenViewModel.getLastToken()
        tokenViewModel.token.observe(this) { token ->
            if (token == null) {
                return@observe
            }

            val authHeader = "Bearer ${token.accessToken}"
            userViewModel.getUser(userId, authHeader)
        }
    }

    private fun observeUser() {
        userViewModel.userSearched.observe(this) { response ->
            if (response == null) {
                return@observe
            }

            if (!Verifier.verifySuccessfulResponse(
                    response,
                    this,
                    positiveAction = { finish() },
                    positiveActionText = getString(R.string.dialog_ok)
                )
            ) {
                return@observe
            }

            val user = response.body()!!

            binding.profileProgressBar.visibility = View.GONE
            binding.profileContentLayout.visibility = View.VISIBLE

            binding.clientNameText.text = user.username
            binding.memberSinceText.text =
                "${getString(R.string.member_since)} ${TextUtils.formatDate(user.createdAt.toLocalDate())}"

            binding.phoneText.text = user.phoneNumber
            binding.emailText.text = user.emailAddress

            if (user.s3ImageUrl != null) {
                binding.clientImage.load(user.s3ImageUrl) {
                    crossfade(true)
                    error(R.drawable._logoslotify_retocado)
                }
            } else {
                binding.clientImage.setImageResource(R.drawable._logoslotify_retocado)
            }
        }
    }
}
