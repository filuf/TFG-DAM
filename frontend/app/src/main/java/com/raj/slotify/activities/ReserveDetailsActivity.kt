package com.raj.slotify.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.raj.slotify.R
import com.raj.slotify.databinding.ActivityReserveDetailsBinding
import com.raj.slotify.dtos.reserves.CompanyReserveSummary
import com.raj.slotify.dtos.reserves.ReserveSummary
import com.raj.slotify.dtos.reserves.UserReserveSummary
import com.raj.slotify.dtos.service.ServiceSummary
import com.raj.slotify.tools.TextUtils
import com.raj.slotify.tools.Verifier
import com.raj.slotify.viewModels.apiRest.ReservesViewModel
import com.raj.slotify.viewModels.apiRest.ServiceViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel
import com.raj.slotify.viewModels.room.TokenViewModel
import java.time.LocalDateTime

class ReserveDetailsActivity : AppCompatActivity() {

    private val tokenViewModel: TokenViewModel by viewModels()
    private val serviceViewModel: ServiceViewModel by viewModels()
    private val reservesViewModel: ReservesViewModel by viewModels()
    private val userDataViewModel: UserDataViewModel by viewModels()

    private lateinit var binding: ActivityReserveDetailsBinding
    private lateinit var authHeader: String

    private var serviceWithSchedules: ServiceSummary? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityReserveDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val paddingButton = if (imeInsets.bottom > 0) imeInsets.bottom else systemBars.bottom

            v.setPadding(systemBars.left, 0, systemBars.right, paddingButton)
            binding.materialToolbar.setPadding(0, systemBars.top, 0, 0)

            insets
        }
        val window = this.window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        binding.detailsProgressBar.visibility = View.VISIBLE
        binding.detailsLayout.visibility = View.GONE

        setUpToolbar()
        observeService()

        val reserveSummary = getReserveSummary()

        if(reserveSummary != null) {
            setReserveCanceledBehaviour(reserveSummary)
            setReserveCompletedBehaviour(reserveSummary)

            obtainServiceDetails(reserveSummary)
            setCardViewDetails(reserveSummary)
            setCancelBehaviour(reserveSummary)
        }
    }

    private fun getReserveSummary(): ReserveSummary? {
        val reserveSummary = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("EXTRA_RESERVE", ReserveSummary::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<ReserveSummary>("EXTRA_RESERVE")
        }
        return reserveSummary
    }

    private fun setReserveCompletedBehaviour(reserveSummary: ReserveSummary) {
        if (TextUtils.calculateCompletedReserve(reserveSummary.endDateTime)) {
            val button = binding.cancelReserveButton
            button.isEnabled = false
            button.text = getString(R.string.completed_reserve)

            binding.reserveCompletedCardView.visibility = View.VISIBLE
        }
    }

    private fun setReserveCanceledBehaviour(reserveSummary: ReserveSummary) {
        if (reserveSummary.isCanceled) {
            val button = binding.cancelReserveButton
            button.isEnabled = false
            button.text = getString(R.string.candeled_reserve)

            binding.reserveCanceledCardView.visibility = View.VISIBLE
        }
    }

    private fun setCancelBehaviour(reserveSummary: ReserveSummary) {
        binding.cancelReserveButton.setOnClickListener {
            reservesViewModel.cancelReserve(reserveSummary.reserveId, authHeader)
        }

        reservesViewModel.reserveCanceledResponse.observe(this) { response ->
            if (response == null)
                return@observe
            else if (!Verifier.verifySuccessfulResponse(response, this, positiveActionText = getString(
                    R.string.dialog_ok)))
                return@observe

            Log.i("RESERVE CANCELED, RESPONSE: ", "message: ${response.message()}, body: ${response.body()}")

            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.reserve_successfully_canceled)
                .setPositiveButton(R.string.dialog_ok) { dialog, _ ->
                    finish()
                }.show()
        }
    }

    private fun setUpToolbar() {
        val toolbar = binding.materialToolbar

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setCardViewDetails(reserveSummary: ReserveSummary) {

        when(reserveSummary) {
            is UserReserveSummary -> {
                // SET IMAGE VIEW
                val imageView = binding.companyImage
                val s3ImageUrl: String? = reserveSummary.companyImageUrl

                if (s3ImageUrl != null)
                    imageView.setImageURI(s3ImageUrl.toUri())

                // MAP
                binding.clientDataLayout.visibility = View.GONE
                binding.enterpriseDataLoyout.visibility = View.VISIBLE

                userDataViewModel.setServiceLocation(reserveSummary.companyPhysicalAddress)

                // MAP CARD DATA
                binding.reserveSite.text = reserveSummary.companyName
                binding.reserveDirection.text = reserveSummary.companyPhysicalAddress

                // OPEN COMPANY PROFILE ON CLICK
                binding.cardDirectionInfo.setOnClickListener {
                    val intent = Intent(this, CompanyProfileActivity::class.java)
                    intent.putExtra("COMPANY_ID", reserveSummary.companyId.toString())
                    startActivity(intent)
                }
            }
            is CompanyReserveSummary -> {
                // CLIENT NAME TEXT
                binding.enterpriseDataLoyout.visibility = View.GONE
                binding.clientDataLayout.visibility = View.VISIBLE

                binding.clientName.text = reserveSummary.userName

                // OPEN CLIENT PROFILE ON CLICK
                binding.clientDataLayout.setOnClickListener {
                    val intent = Intent(this, ClientProfileActivity::class.java)
                    intent.putExtra("USER_ID", reserveSummary.userId.toString())
                    startActivity(intent)
                }
            }
        }

        binding.priceText.text = TextUtils.formatPrice(reserveSummary.servicePriceCent)

        // SET TEXT VIEWS
        val startTime: LocalDateTime = reserveSummary.startDateTime
        val endTime: LocalDateTime = reserveSummary.endDateTime

        binding.timeText.text = TextUtils.formatTime(startTime.toLocalTime())
        binding.dateText.text = TextUtils.formatDate(startTime.toLocalDate())

        val duration = reserveSummary.minutesDuration

        binding.reserveDurationText.text =
            "$duration ${if (duration == 1) getString(R.string.minute_word) else getString(R.string.minutes_word)}"

        binding.remainingText.text = "(${TextUtils.formatRemainingText(startTime, endTime, this).trim()})"
    }

    private fun obtainServiceDetails(reserveSummary: ReserveSummary) {
        tokenViewModel.getLastToken()
        tokenViewModel.token.observe(this) { token ->
            if (token == null) {
                return@observe
            }

            authHeader = "Bearer ${token.accessToken}"

            binding.serviceNameText.text = reserveSummary.serviceName
            val description = serviceWithSchedules?.description

            if (description.isNullOrEmpty()) {
                binding.descText.visibility = View.GONE
            } else {
                binding.descText.visibility = View.VISIBLE
                binding.descText.text = description
            }

            // GET SERVICE TO OBTAIN DESCRIPTION
            serviceViewModel.getServiceWithSchedules(reserveSummary.serviceId, authHeader)
            serviceViewModel.serviceWithSchedules.observe(this) { response ->
                if (response != null) {
                    if (response.isSuccessful && response.body() != null) {
                        val serviceWithSchedules = response.body()!!
                        binding.descText.text = serviceWithSchedules.description

                        binding.detailsProgressBar.visibility = View.GONE
                        binding.detailsLayout.visibility = View.VISIBLE
                    } else {
                        Log.e(
                            "ERROR",
                            "Error al obtener el servicio: ${response.code()}: ${response.body()}"
                        )
                    }
                }
            }
        }
    }

    private fun observeService() {

        serviceViewModel.serviceWithSchedules.observe(this) { response ->
            if (response != null) {
                if (response.isSuccessful && response.body() != null) {
                    val serviceWithSchedules = response.body()!!
                    this.serviceWithSchedules = serviceWithSchedules
                } else {
                    Log.e(
                        "ERROR",
                        "Error al obtener el servicio: ${response.code()}: ${response.body()}"
                    )
                }
            }
        }
    }
}