package com.raj.slotify.fragments.reserves.client

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import com.raj.slotify.R
import com.raj.slotify.databinding.FragmentReserveDetailsClientBinding
import com.raj.slotify.dtos.reserves.ReserveSummary
import com.raj.slotify.dtos.service.ServiceSummary
import com.raj.slotify.viewModels.apiRest.ReservesViewModel
import com.raj.slotify.viewModels.apiRest.ServiceViewModel
import com.raj.slotify.viewModels.frontend.LayoutViewModel
import com.raj.slotify.viewModels.frontend.MainViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel
import java.time.LocalDateTime

class ReserveDetailsFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val layoutViewModel: LayoutViewModel by activityViewModels()
    private val userDataViewModel: UserDataViewModel by activityViewModels()
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
        layoutViewModel.setSuperiorFragmentVisibility(View.GONE)

        binding = FragmentReserveDetailsClientBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userToken = "Bearer ${userDataViewModel.userToken.value?.accessToken?:""}"

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

        reservesViewModel.reserveSearched.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                if (response.isSuccessful && response.body() != null) {
                    val reserveSummary: ReserveSummary = response.body()!!

                    serviceViewModel.getServiceWithSchedules(
                        reserveSummary.serviceId,
                        userToken
                    )

                    if (serviceWithSchedules != null) {

                        // SET TEXT VIEWS
                        binding.serviceNameText.text = serviceWithSchedules!!.serviceName
                        binding.descText.text = serviceWithSchedules!!.description

                        // COMPANY CARD VIEW
                        binding.companyImage.setImageURI(serviceWithSchedules!!.s3ImageKey.toUri())

                        // TODO: SETEAR IMAGEN, TITULO Y DIRECCION DE LA EMPRESA


                        val price: Double = (serviceWithSchedules!!.servicePriceCent/100).toDouble()
                        binding.priceText.text = "$price ${getString(R.string.coint_format)}"

                        val startTime: LocalDateTime = reserveSummary.startDateTime
                        binding.timeText.text = "${startTime.hour}:${startTime.minute}"
                        binding.dateText.text = "${startTime.dayOfMonth}/${startTime.monthValue}/${startTime.year}"

                        binding.reserveDurationText.text = "${serviceWithSchedules!!.serviceMinutesDuration} ${getString(R.string.minutes_word)}"

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
                } else {
                    Log.e("ERROR", "Error al obtener la reserva: ${response.code()}: ${response.body()}")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        layoutViewModel.setSuperiorFragmentVisibility(View.VISIBLE)
    }

}