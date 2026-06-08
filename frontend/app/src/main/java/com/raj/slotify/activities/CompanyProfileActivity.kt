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
import com.raj.slotify.databinding.ActivityCompanyProfileBinding
import com.raj.slotify.tools.Verifier
import com.raj.slotify.viewModels.apiRest.CompanyViewModel
import com.raj.slotify.viewModels.room.TokenViewModel
import java.util.UUID

class CompanyProfileActivity : AppCompatActivity() {

    private val companyViewModel: CompanyViewModel by viewModels()
    private val tokenViewModel: TokenViewModel by viewModels()

    private lateinit var binding: ActivityCompanyProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCompanyProfileBinding.inflate(layoutInflater)
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
        observeCompany()

        val companyId = intent.getStringExtra("COMPANY_ID")?.let { UUID.fromString(it) }

        if (companyId == null) {
            Log.e("CompanyProfileActivity", "No se ha recibido el ID de la empresa")
            finish()
            return
        }

        loadCompany(companyId)
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.materialToolbar)
        binding.materialToolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadCompany(companyId: UUID) {
        tokenViewModel.getLastToken()
        tokenViewModel.token.observe(this) { token ->
            if (token == null) {
                return@observe
            }

            val authHeader = "Bearer ${token.accessToken}"
            companyViewModel.getCompanyById(companyId, authHeader)
        }
    }

    private fun observeCompany() {
        companyViewModel.company.observe(this) { response ->
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

            val company = response.body()!!

            binding.profileProgressBar.visibility = View.GONE
            binding.profileContentLayout.visibility = View.VISIBLE

            binding.companyNameText.text = company.companyName

            if (company.rattingAvg.isNullOrEmpty()) {
                binding.ratingBar.visibility = View.GONE
            } else {
                binding.ratingBar.rating = company.rattingAvg.toFloat()
            }

            if (company.description.isNullOrEmpty()) {
                binding.descriptionText.visibility = View.GONE
            } else {
                binding.descriptionText.text = company.description
            }

            binding.addressText.text = company.physicalAddress
            binding.phoneText.text = company.phoneNumber ?: getString(R.string.empty_string)
            binding.emailText.text = company.emailAddress

            if (company.s3ImageUrl != null) {
                binding.companyImage.load(company.s3ImageUrl) {
                    crossfade(true)
                    error(R.drawable._logoslotify_retocado)
                }
            } else {
                binding.companyImage.setImageResource(R.drawable._logoslotify_retocado)
            }
        }
    }
}
