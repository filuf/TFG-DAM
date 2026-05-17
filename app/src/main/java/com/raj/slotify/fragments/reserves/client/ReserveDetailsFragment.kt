package com.raj.slotify.fragments.reserves.client

import android.os.Bundle
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

        val userToken = "Bearer ${userDataViewModel.accessToken.value?:""}"

        serviceViewModel.serviceWithSchedules.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                if (response.isSuccessful && response.body() != null) {
                    val serviceWithSchedules = response.body()!!
                    this.serviceWithSchedules = serviceWithSchedules
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
                            val remainingDays = startTime.dayOfYear.toLong() - now.dayOfYear.toLong()
                            val remainingHours = startTime.hour.toLong() - now.hour.toLong()
                            val remainingMinutes = startTime.minute.toLong() - now.minute.toLong()

                            var textRemaining = ""

                            textRemaining = if (remainingDays > 0) {
                                " $remainingDays ${getString(R.string.days_word)} ${getString(R.string.left_after)} "
                            } else if (remainingHours > 0) {
                                " $remainingHours ${getString(R.string.hours_word)} ${getString(R.string.left_after)} "
                            } else {
                                " $remainingMinutes ${getString(R.string.minutes_word)} ${getString(R.string.left_after)} "
                            }
                            return textRemaining
                        }

                        binding.remainingText.text = "(${calculateRemainingText(startTime)})"

                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        layoutViewModel.setSuperiorFragmentVisibility(View.VISIBLE)
    }

}