package com.raj.slotify.fragments.reserves.client.createReserve

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.raj.slotify.R
import com.raj.slotify.databinding.FragmentAssureCorrectReserveInformationBinding
import com.raj.slotify.dtos.company.GetCompanyResponse
import com.raj.slotify.dtos.company.GetServicesResponse
import com.raj.slotify.dtos.reserves.CreateReserveRequest
import com.raj.slotify.models.TextModel
import com.raj.slotify.tools.TextUtils
import com.raj.slotify.viewModels.apiRest.ReservesViewModel
import com.raj.slotify.viewModels.frontend.ClientReservesViewModel
import com.raj.slotify.viewModels.frontend.LayoutViewModel
import com.raj.slotify.viewModels.frontend.MainViewModel
import com.raj.slotify.viewModels.room.TokenViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class AssureCorrectReserveInformationFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val layoutViewModel: LayoutViewModel by activityViewModels()
    private val clientReservesViewModel: ClientReservesViewModel by activityViewModels()
    private val tokenViewModel: TokenViewModel by activityViewModels()
    private val reservesViewModel: ReservesViewModel by activityViewModels()

    private lateinit var binding: FragmentAssureCorrectReserveInformationBinding
    private lateinit var startDateTime: LocalDateTime

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layoutViewModel.setDescriptionVisibility(View.GONE)

        layoutViewModel.setConfirmButtonVisibility(View.VISIBLE)
        layoutViewModel.setBackButtonVisibility(View.VISIBLE)
        layoutViewModel.setCancelButtonVisibility(View.GONE)

        mainViewModel.setTitle(TextModel(R.string.check_info))

        binding = FragmentAssureCorrectReserveInformationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setConfirmButtonBehaviour()
        listenDateTime()
        setReserveDetailsTexts()

    }

    fun setReserveDetailsTexts() {
        val service: GetServicesResponse = clientReservesViewModel.serviceToReserve.value ?: return
        val company: GetCompanyResponse = clientReservesViewModel.companyToReserve.value?: return
        val minutes = service.serviceMinutesDuration
        val minutesText = if (minutes == 1) getString(R.string.minute_word) else getString(R.string.minutes_word)

        binding.reserveServiceText.text = service.serviceName
        binding.reservePriceText.text = TextUtils.formatPrice(service.servicePriceCent)
        binding.reserveServiceDurationText.text = "$minutes $minutesText"

        binding.companyNameText.text = company.companyName
        binding.companyDirectionText.text = company.physicalAddress
    }

    fun listenDateTime() {
        clientReservesViewModel.dateTimeReserve.observe(viewLifecycleOwner) { startTime ->
            if (startTime == null)
                return@observe

            val timeText = TextUtils.formatTime(startTime.toLocalTime())
            val dateText = TextUtils.formatDate(startTime.toLocalDate())

            binding.reserveServiceTime.text = timeText
            binding.reserveServiceDateText.text = dateText

            startDateTime = startTime
        }
    }

    fun setConfirmButtonBehaviour() {
        viewLifecycleOwner.lifecycleScope.launch {
            layoutViewModel.confirmButtonClicked.collect {
                layoutViewModel.setConfirmButtonVisibility(View.GONE)
                Log.i("CONFIRM", "Confirm button clicked")

                val serviceId = clientReservesViewModel.serviceToReserve.value?.serviceId

                if (serviceId == null) {
                    Toast.makeText(requireContext(), "datetime: $startDateTime | serviceId: $serviceId", Toast.LENGTH_SHORT)
                        .show()

                    return@collect
                }

                reservesViewModel.createReserve(
                    authHeader = "Bearer ${tokenViewModel.token.value?.accessToken?:""}",
                    createReserveRequest = CreateReserveRequest(startDateTime, serviceId)
                )

                view?.findNavController()?.navigate(R.id.action_assureCorrectReserveInformationFragment_to_reserveLastScreenFragment)
            }
        }
    }

}