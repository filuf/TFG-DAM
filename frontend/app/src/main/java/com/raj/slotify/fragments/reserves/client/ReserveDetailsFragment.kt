package com.raj.slotify.fragments.reserves.client

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.raj.slotify.R
import com.raj.slotify.databinding.FragmentReserveDetailsClientBinding
import com.raj.slotify.dtos.reserves.CompanyReserveSummary
import com.raj.slotify.dtos.reserves.UserReserveSummary
import com.raj.slotify.dtos.service.ServiceSummary
import com.raj.slotify.viewModels.apiRest.ReservesViewModel
import com.raj.slotify.viewModels.apiRest.ServiceViewModel
import com.raj.slotify.viewModels.frontend.ClientReservesViewModel
import com.raj.slotify.viewModels.frontend.LayoutViewModel
import com.raj.slotify.viewModels.frontend.MainViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel
import java.time.LocalDateTime

class ReserveDetailsFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val layoutViewModel: LayoutViewModel by activityViewModels()
    private val userDataViewModel: UserDataViewModel by activityViewModels()
    private val clientReservesViewModel: ClientReservesViewModel by activityViewModels()
    private val serviceViewModel: ServiceViewModel by activityViewModels()

    private val reservesViewModel: ReservesViewModel by activityViewModels()

    private lateinit var binding: FragmentReserveDetailsClientBinding

    private var serviceWithSchedules: ServiceSummary? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layoutViewModel.setNavBottomVisibility(View.GONE)

        binding = FragmentReserveDetailsClientBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // BACK BUTTON
        binding.linearLayout.setOnClickListener {
            view.findNavController().popBackStack()
        }

        serviceViewModel.serviceWithSchedules.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                if (response.isSuccessful && response.body() != null) {
                    val serviceWithSchedules = response.body()!!
                    this.serviceWithSchedules = serviceWithSchedules
                } else {
                    Log.e("ERROR", "Error al obtener el servicio: ${response.code()}: ${response.body()}")
                }
            }
        }

        val userToken = "Bearer ${userDataViewModel.userToken.value?.accessToken?:""}"

        clientReservesViewModel.lastReserveSelected.observe(viewLifecycleOwner) { reserveSummary ->

            binding.serviceNameText.text = reserveSummary.serviceName
            binding.descText.text = serviceWithSchedules?.description?:"null"

            // GET SERVICE TO OBTAIN DESCRIPTION
            serviceViewModel.getServiceWithSchedules(reserveSummary.serviceId, userToken)
            serviceViewModel.serviceWithSchedules.observe(viewLifecycleOwner) { response ->
                if (response != null) {
                    if (response.isSuccessful && response.body() != null) {
                        val serviceWithSchedules = response.body()!!
                        binding.descText.text = serviceWithSchedules.description
                    } else {
                        Log.e("ERROR", "Error al obtener el servicio: ${response.code()}: ${response.body()}")
                    }
                }
            }

            when (reserveSummary) {
                is UserReserveSummary -> {

                    // COMPANY CARD VIEW
                    val imageView = binding.companyImage
                    val s3ImageUrl: String? = reserveSummary.companyImageUrl

                    if (s3ImageUrl != null)
                        imageView.setImageURI(reserveSummary.companyImageUrl.toUri())

                    val price: Double = (reserveSummary.servicePriceCent/100).toDouble()
                    binding.priceText.text = "$price ${getString(R.string.coint_format)}"

                    // MAP
                    userDataViewModel.setServiceLocation(reserveSummary.companyPhysicalAddress)

                    // MAP CARD DATA
                    binding.reserveSite.text = reserveSummary.companyName
                    binding.reserveDirection.text = reserveSummary.companyPhysicalAddress

                }
                is CompanyReserveSummary -> {

                    //binding.companyImage.

                }
            }

            // SET TEXT VIEWS


            val startTime: LocalDateTime = reserveSummary.startDateTime
            binding.timeText.text = "${startTime.hour}:${startTime.minute}"
            binding.dateText.text = "${startTime.dayOfMonth}/${startTime.monthValue}/${startTime.year}"

            binding.reserveDurationText.text = "${reserveSummary.minutesDuration} ${getString(R.string.minutes_word)}"

            fun calculateRemainingText(startTime: LocalDateTime): String {
                // SET REMAINING TEXT
                val now: LocalDateTime = LocalDateTime.now()

                val duration = java.time.Duration.between(now, startTime)
                if (duration.isNegative || duration.isZero) {
                    return getString(R.string.already_started)
                }

                val remainingDays = duration.toDays()
                val remainingHours = duration.toHours()
                val remainingMinutes = duration.toMinutes()

                return when {
                    remainingDays > 0 -> " $remainingDays ${ if (remainingDays > 1) getString(R.string.days_word) else getString(R.string.day_word)} ${getString(R.string.left_after)} "
                    remainingHours > 0 -> " $remainingHours ${ if (remainingHours > 1) getString(R.string.hours_word) else getString(R.string.hour_word)} ${getString(R.string.left_after)} "
                    else -> " $remainingMinutes ${ if (remainingMinutes > 1) getString(R.string.minutes_word) else getString(R.string.minute_word)} ${getString(R.string.left_after)} "
                }
            }

            binding.remainingText.text = "(${calculateRemainingText(startTime)})"

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        layoutViewModel.setNavBottomVisibility(View.VISIBLE)
    }

}