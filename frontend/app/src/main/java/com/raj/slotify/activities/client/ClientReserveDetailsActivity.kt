package com.raj.slotify.activities.client

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
import com.raj.slotify.databinding.ActivityClientReserveDetailsBinding
import com.raj.slotify.dtos.reserves.UserReserveSummary
import com.raj.slotify.dtos.service.ServiceSummary
import com.raj.slotify.tools.FormatUtils
import com.raj.slotify.tools.Verifier
import com.raj.slotify.viewModels.apiRest.ReservesViewModel
import com.raj.slotify.viewModels.apiRest.ServiceViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel
import com.raj.slotify.viewModels.room.TokenViewModel
import java.time.Duration
import java.time.LocalDateTime
import kotlin.getValue

class ClientReserveDetailsActivity : AppCompatActivity() {

    private val tokenViewModel: TokenViewModel by viewModels()
    private val serviceViewModel: ServiceViewModel by viewModels()
    private val reservesViewModel: ReservesViewModel by viewModels()
    private val userDataViewModel: UserDataViewModel by viewModels()

    private lateinit var binding: ActivityClientReserveDetailsBinding
    private lateinit var authHeader: String

    private var serviceWithSchedules: ServiceSummary? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityClientReserveDetailsBinding.inflate(layoutInflater)
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

        setUpToolbar()
        observeService()

        val reserveSummary = getReserveSummary()

        if(reserveSummary != null) {
            obtainServiceDetails(reserveSummary)
            setCardViewDetails(reserveSummary)
            setCancelBehaviour(reserveSummary)
        }
    }

    private fun setCancelBehaviour(reserveSummary: UserReserveSummary) {
        binding.cancelReserveButton.setOnClickListener {
            reservesViewModel.cancelReserve(reserveSummary.reserveId, authHeader)
        }

        reservesViewModel.reserveCanceledResponse.observe(this) { response ->
            if (response == null)
                return@observe
            else if (!Verifier.verifySuccessfulResponse(response, this, positiveActionText = getString(R.string.dialog_ok)))
                return@observe

            Log.i("RESERVE CANCELED, RESPONSE: ", "message: ${response.message()}, body: ${response.body()}")

            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.reserve_successfully_canceled)
                .setMessage(R.string.reserve_successfully_canceled)
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

    private fun setCardViewDetails(reserveSummary: UserReserveSummary) {
        // COMPANY CARD VIEW
        val imageView = binding.companyImage
        val s3ImageUrl: String? = reserveSummary.companyImageUrl

        if (s3ImageUrl != null)
            imageView.setImageURI(s3ImageUrl.toUri())

        binding.priceText.text = FormatUtils.formatPrice(reserveSummary.servicePriceCent)

        // MAP
        userDataViewModel.setServiceLocation(reserveSummary.companyPhysicalAddress)

        // MAP CARD DATA
        binding.reserveSite.text = reserveSummary.companyName
        binding.reserveDirection.text = reserveSummary.companyPhysicalAddress

        // SET TEXT VIEWS
        val startTime: LocalDateTime = reserveSummary.startDateTime
        binding.timeText.text = FormatUtils.formatTime(startTime.toLocalTime())
        binding.dateText.text = FormatUtils.formatDate(startTime.toLocalDate())

        val duration = reserveSummary.minutesDuration

        binding.reserveDurationText.text =
            "$duration ${if (duration == 1) getString(R.string.minute_word) else getString(R.string.minutes_word)}"

        fun calculateRemainingText(startTime: LocalDateTime): String {
            // SET REMAINING TEXT
            val now: LocalDateTime = LocalDateTime.now()

            val duration = Duration.between(now, startTime)
            if (duration.isNegative || duration.isZero) {
                return getString(R.string.already_started)
            }

            val remainingDays = duration.toDays()
            val remainingHours = duration.toHours()
            val remainingMinutes = duration.toMinutes()

            return when {
                remainingDays > 0 -> " $remainingDays ${
                    if (remainingDays > 1) getString(R.string.days_word) else getString(
                        R.string.day_word
                    )
                } ${getString(R.string.left_after)} "

                remainingHours > 0 -> " $remainingHours ${
                    if (remainingHours > 1) getString(R.string.hours_word) else getString(
                        R.string.hour_word
                    )
                } ${getString(R.string.left_after)} "

                else -> " $remainingMinutes ${
                    if (remainingMinutes > 1) getString(R.string.minutes_word) else getString(
                        R.string.minute_word
                    )
                } ${getString(R.string.left_after)} "
            }
        }

        binding.remainingText.text = "(${calculateRemainingText(startTime).trim()})"
    }

    private fun obtainServiceDetails(reserveSummary: UserReserveSummary) {
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

    private fun getReserveSummary(): UserReserveSummary? {
        val reserveSummary = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("EXTRA_RESERVE", UserReserveSummary::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<UserReserveSummary>("EXTRA_RESERVE")
        }
        return reserveSummary
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