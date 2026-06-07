package com.raj.slotify.fragments.reserves.client.createReserve

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.raj.slotify.R
import com.raj.slotify.adapters.ListScheduleIntervalsAdapter
import com.raj.slotify.databinding.FragmentSelectReserveDetailsBinding
import com.raj.slotify.dtos.company.GetServicesResponse
import com.raj.slotify.dtos.reserves.TimeIntervalDTO
import com.raj.slotify.models.TextModel
import com.raj.slotify.tools.TextUtils
import com.raj.slotify.tools.Verifier
import com.raj.slotify.viewModels.apiRest.ServiceViewModel
import com.raj.slotify.viewModels.frontend.ClientReservesViewModel
import com.raj.slotify.viewModels.frontend.LayoutViewModel
import com.raj.slotify.viewModels.frontend.MainViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel
import com.raj.slotify.viewModels.room.TokenViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class SelectReserveDetailsFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val layoutViewModel: LayoutViewModel by activityViewModels()
    private val tokenViewModel: TokenViewModel by activityViewModels()
    private val clientReservesViewModel: ClientReservesViewModel by activityViewModels()
    private val serviceViewModel: ServiceViewModel by activityViewModels()
    private val userDataViewModel: UserDataViewModel by activityViewModels()

    private lateinit var binding: FragmentSelectReserveDetailsBinding
    private lateinit var service: GetServicesResponse
    private lateinit var localDate: LocalDate
    private lateinit var slots: List<TimeIntervalDTO>
    private lateinit var selectedTime: LocalTime

    private lateinit var start: LocalTime
    private lateinit var end: LocalTime

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layoutViewModel.setExplicationVisibility(View.GONE)

        layoutViewModel.setConfirmButtonVisibility(View.GONE)
        layoutViewModel.setCancelButtonVisibility(View.VISIBLE)

        binding = FragmentSelectReserveDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layoutSectedHour.visibility = View.INVISIBLE
        binding.selectIntervalsLayout.visibility = View.GONE

        this.slots = emptyList()

        listenService()
        configCalendar()

        val adapter = setupRecycler()
        listenSchedules(adapter)

        configSeekBar()
        setConfirmButtonListener()
    }

    fun setConfirmButtonListener() {
        val button = binding.confirmDateTimeButton
        button.isEnabled = false

        button.setOnClickListener {
            if (slots.isEmpty()) {
                return@setOnClickListener
            }

            val slotsList = slots.map{ slot ->
                "${TextUtils.formatTime(slot.startTime)} - ${TextUtils.formatTime(slot.endTime)}"
            }.toTypedArray()

            val slotIsValid = slots.any { slot -> (
                        slot.startTime.isBefore(selectedTime) &&
                        slot.endTime.isAfter(selectedTime)
                    ) ||
                    slot.startTime == selectedTime ||
                    slot.endTime == selectedTime
            }

            if (!slotIsValid) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.invalid_time_title)
                    .setItems(slotsList) { _, which ->
                        val selectedSlot = slots[which]
                        selectedTime = selectedSlot.startTime
                        binding.selectedHourText.text = TextUtils.formatTime(selectedTime)
                        binding.slotsSeekBar.progress = selectedTime.toSecondOfDay() - start.toSecondOfDay()
                    }
                    .setPositiveButton(R.string.dialog_ok, null)
                    .show()
            }
            else {
                val localDateTime = LocalDateTime.of(localDate, selectedTime)
                clientReservesViewModel.setDateTimeReserve(localDateTime)

                view?.findNavController()?.navigate(R.id.action_selectReserveDetailsFragment_to_assureCorrectReserveInformationFragment)
            }
        }
    }

    fun listenSchedules(adapter: ListScheduleIntervalsAdapter) {
        serviceViewModel.clearSlots()

        serviceViewModel.serviceSlotsAvailable.observe(viewLifecycleOwner) { slots ->
            // VERIFY SUCCESSFUL RESPONSE
            if (slots == null || !Verifier.verifySuccessfulResponse(
                slots,
                requireContext(),
                positiveActionText = getString(R.string.dialog_ok)
            )) {
                binding.layoutSectedHour.visibility = View.INVISIBLE
                binding.selectIntervalsLayout.visibility = View.GONE
                binding.confirmDateTimeButton.isEnabled = false

                return@observe
            }

            // IF NOT BODY, NOT SHOW ANYTHING
            val slotsResponse = slots.body()
            if (slotsResponse.isNullOrEmpty()) {

                binding.layoutSectedHour.visibility = View.INVISIBLE
                binding.selectIntervalsLayout.visibility = View.GONE
                binding.confirmDateTimeButton.isEnabled = false
                binding.noSlotsText.visibility = View.VISIBLE

                this.slots = emptyList()
                return@observe
            }

            binding.noSlotsText.visibility = View.GONE
            binding.selectIntervalsLayout.visibility = View.VISIBLE

            this.slots = slotsResponse
            adapter.setItems(slotsResponse)
        }
    }

    fun configSeekBar() {
        binding.layoutSectedHour.visibility = View.INVISIBLE

        binding.slotsSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (!::start.isInitialized) return

                val selectedTime = start.plusSeconds(progress.toLong())

                this@SelectReserveDetailsFragment.selectedTime = selectedTime

                binding.selectedHourText.text = TextUtils.formatTime(selectedTime)
                binding.confirmDateTimeButton.isEnabled = true
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    fun listenService() {
        clientReservesViewModel.serviceToReserve.observe(viewLifecycleOwner) { service ->
            this.service = service

            mainViewModel.setTitle(TextModel(customText = service.serviceName))
            mainViewModel.setSubtitle(TextModel(customText = TextUtils.formatPrice(service.servicePriceCent)))

            val company = clientReservesViewModel.companyToReserve.value
            if (company != null) {

                if (company.s3ImageKey != null)
                    binding.companyImage.load(company.s3ImageKey) {
                        crossfade(true)
                        placeholder(R.drawable._logoslotify_retocado) // Pon un placeholder si tienes
                    }

                binding.serviceCompanyName.text = company.companyName

                val address = company.physicalAddress

                binding.serviceDirection.text = address
                userDataViewModel.setServiceLocation(address)
            }
        }
    }

    fun setupRecycler(): ListScheduleIntervalsAdapter {
        val recycler = binding.slotsRecycler

        val adapter = ListScheduleIntervalsAdapter(mutableListOf()) { selectedSlot ->

            // DESELECT OTHER ITEMS
            slots
                .filter { slot -> slot != selectedSlot }
                .forEach { slot -> deselectSlot(slot) }

            this.start = selectedSlot.startTime
            this.end = selectedSlot.endTime

            Log.i("LISTEN SCHEDULES", "start: $start, end: $end")

            val difference = end.toSecondOfDay() - start.toSecondOfDay()

            val seekBar = binding.slotsSeekBar
            seekBar.min = 0
            seekBar.max = difference
            seekBar.progress = 0

            // SHOW TEXT
            selectedTime = start
            binding.selectedHourText.text = TextUtils.formatTime(start)

            //SHOW LAYOUT
            binding.layoutSectedHour.visibility = View.VISIBLE
            binding.confirmDateTimeButton.isEnabled = true
        }

        recycler.adapter = adapter
        recycler.layoutManager = GridLayoutManager(requireContext(), 2)

        return adapter
    }

    fun deselectSlot(slot: TimeIntervalDTO) {
        val recycler = binding.slotsRecycler

        val position = slots.indexOf(slot)
        val viewHolder = recycler.findViewHolderForAdapterPosition(position) as ListScheduleIntervalsAdapter.ViewHolder

        val cardView = viewHolder.itemView as com.google.android.material.card.MaterialCardView
        cardView.strokeWidth = 0

        val textView = viewHolder.intervalText
        textView.setTextColor(textView.context.resources.getColor(R.color.md_theme_onSurfaceVariant))
        textView.setTypeface(null, android.graphics.Typeface.NORMAL)

        binding.layoutSectedHour.visibility = View.INVISIBLE
        binding.confirmDateTimeButton.isEnabled = false
    }

    fun configCalendar() {
        val calendar = binding.selectReserveDayCalendar

        val dateToReserve = clientReservesViewModel.dateTimeReserve.value

        if (dateToReserve != null) {
            this.localDate = dateToReserve.toLocalDate()
            calendar.date = dateToReserve.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }

        calendar.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val localDate = LocalDate.of(year, month+1, dayOfMonth)

            // DESELECT ALL SLOT WHEN UPDATE CALENDAR DATE
            slots.forEach { slot -> deselectSlot(slot) }

            this.localDate = localDate

            val stringDate = localDate.toString()
            Log.i("LOCALDATE", stringDate)

            tokenViewModel.token.observe(viewLifecycleOwner) { token ->
                if (token == null) {
                    return@observe
                }

                val accessToken = token.accessToken
                val authHeader = "Bearer $accessToken"

                Log.i("AuthHeader", authHeader)

                serviceViewModel.getServiceSlotsAvailable(service.serviceId, authHeader, stringDate)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        clientReservesViewModel.setDateTimeReserve(null)
        binding.layoutSectedHour.visibility = View.INVISIBLE
        binding.confirmDateTimeButton.isEnabled = false

    }

}