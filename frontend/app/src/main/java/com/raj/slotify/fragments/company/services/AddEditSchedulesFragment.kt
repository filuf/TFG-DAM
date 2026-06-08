package com.raj.slotify.fragments.company.services

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.raj.slotify.R
import com.raj.slotify.databinding.FragmentAddEditSchedulesBinding
import com.raj.slotify.dtos.service.CreateServiceScheduleRequest
import com.raj.slotify.dtos.service.PatchScheduleRequest
import com.raj.slotify.dtos.service.ScheduleSummary
import com.raj.slotify.fragments.company.services.ServicesFragment.Companion.EXTRA_SERVICE_ID
import com.raj.slotify.models.ChipComponent
import com.raj.slotify.tools.TextUtils
import com.raj.slotify.tools.Verifier
import com.raj.slotify.viewModels.apiRest.ScheduleViewModel
import com.raj.slotify.viewModels.apiRest.ServiceViewModel
import com.raj.slotify.viewModels.frontend.EnterpriseDataViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.Calendar
import java.util.UUID

class AddEditSchedulesFragment : Fragment() {

    private val enterpriseDataViewModel: EnterpriseDataViewModel by activityViewModels()
    private val userDataViewModel: UserDataViewModel by activityViewModels()

    private val serviceViewModel: ServiceViewModel by activityViewModels()
    private val schedulesViewModel: ScheduleViewModel by activityViewModels()

    private var schedules: ArrayList<CreateServiceScheduleRequest> = arrayListOf()
    private var schedulesResponse: List<ScheduleSummary> = emptyList()
    private var authHeader: String? = null
    private var serviceId: String? = null

    private lateinit var binding: FragmentAddEditSchedulesBinding

    private lateinit var dayChips: List<Chip>
    private var chipIdMap = mutableMapOf<Chip, UUID>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddEditSchedulesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as AppCompatActivity
        serviceId = activity.intent.getStringExtra(EXTRA_SERVICE_ID)

        getToken()
        setupToolbar()

        recollectChips()
        setupChipListeners()

        observeSchedules()

        setBackButton()

    }

    private fun setupToolbar() {
        val activity = requireActivity() as AppCompatActivity

        activity.setSupportActionBar(binding.editSchedulesToolbar)
        binding.editSchedulesToolbar.setNavigationOnClickListener { view?.findNavController()?.popBackStack() }
    }

    fun getToken() {
        userDataViewModel.authHeader.observe(viewLifecycleOwner) { authHeader ->
            this.authHeader = authHeader
        }
    }

    fun observeSchedules() {
        enterpriseDataViewModel.schedulesOfService.observe(viewLifecycleOwner) { schedules ->
            if (schedules == null) return@observe

            schedules.forEach { schedule ->
                selectChipByDayOfWeek(schedule)
            }
        }
    }

    fun selectChipByDayOfWeek(schedule: ScheduleSummary) {
        var textStart: TextView
        var textEnd: TextView
        var layout: LinearLayout
        var chip: Chip

        val dayOfWeek = schedule.dayOfWeek

        when(dayOfWeek) {
            DayOfWeek.MONDAY.value -> {
                chip = binding.chipMonday
                textStart = binding.mondayStartText
                textEnd = binding.mondayEndText
                layout = binding.mondayChipLayout
            }
            DayOfWeek.TUESDAY.value -> {
                chip = binding.chipTuesday
                textStart = binding.tuesdayStartText
                textEnd = binding.tuesdayEndText
                layout = binding.tuesdayChipLayout
            }
            DayOfWeek.WEDNESDAY.value -> {
                chip = binding.chipWednesday
                textStart = binding.wednesdayStartText
                textEnd = binding.wednesdayEndText
                layout = binding.wednesdayChipLayout
            }
            DayOfWeek.THURSDAY.value -> {
                chip = binding.chipThursday
                textStart = binding.thursdayStartText
                textEnd = binding.thursdayEndText
                layout = binding.thursdayChipLayout
            }
            DayOfWeek.FRIDAY.value -> {
                chip = binding.chipFriday
                textStart = binding.fridayStartText
                textEnd = binding.fridayEndText
                layout = binding.fridayChipLayout
            }
            DayOfWeek.SATURDAY.value -> {
                chip = binding.chipSaturday
                textStart = binding.saturdayStartText
                textEnd = binding.saturdayEndText
                layout = binding.saturdayChipLayout
            }
            DayOfWeek.SUNDAY.value -> {
                chip = binding.chipSunday
                textStart = binding.sundayStartText
                textEnd = binding.sundayEndText
                layout = binding.sundayChipLayout
            }
            else -> {
                chip = binding.chipMonday
                textStart = binding.mondayStartText
                textEnd = binding.mondayEndText
                layout = binding.mondayChipLayout
            }
        }
        chipIdMap[chip] = schedule.id

        chip.isChecked = true
        textStart.text = "${getString(R.string.start_word)}: ${schedule.startTime}"
        textEnd.text = "${getString(R.string.end_word)}: ${schedule.endTime}"
        layout.visibility = View.VISIBLE
    }

    fun setBackButton() {
        binding.goBackSchedulesButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    fun recollectChips() {
        dayChips = listOf(
            binding.chipMonday, binding.chipTuesday, binding.chipWednesday,
            binding.chipThursday, binding.chipFriday, binding.chipSaturday, binding.chipSunday
        )
    }

    fun showTimePicker(onTimeSelected: (LocalTime) -> Unit, title: String) {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
            .setMinute(Calendar.getInstance().get(Calendar.MINUTE))
            .setTitleText(title)
            .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
            .build()

        picker.show(requireActivity().supportFragmentManager, "MATERIAL_TIME_PICKER")

        picker.addOnCancelListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.time_selection_canceled))
                .setMessage(R.string.shedule_selected_will_not_saved)
                .setPositiveButton(getString(R.string.dialog_ok), null)
        }

        picker.addOnPositiveButtonClickListener {
            onTimeSelected(LocalTime.of(picker.hour, picker.minute))
        }
    }

    private fun getChipByStringOfDay(selectedDay: String): ChipComponent {

        var textStart: TextView
        var textEnd: TextView
        val dayOfWeek: DayOfWeek
        val layout: LinearLayout
        var chip: Chip

        when(selectedDay) {
            getString(R.string.monday_word) -> {
                dayOfWeek = DayOfWeek.MONDAY
                chip = binding.chipMonday
                textStart = binding.mondayStartText
                textEnd = binding.mondayEndText
                layout = binding.mondayChipLayout
            }
            getString(R.string.tuesday_word) -> {
                dayOfWeek = DayOfWeek.TUESDAY
                chip = binding.chipTuesday
                textStart = binding.tuesdayStartText
                textEnd = binding.tuesdayEndText
                layout = binding.tuesdayChipLayout
            }
            getString(R.string.wednesday_word) -> {
                dayOfWeek = DayOfWeek.WEDNESDAY
                chip = binding.chipWednesday
                textStart = binding.wednesdayStartText
                textEnd = binding.wednesdayEndText
                layout = binding.wednesdayChipLayout
            }
            getString(R.string.thursday_word) -> {
                dayOfWeek = DayOfWeek.THURSDAY
                chip = binding.chipThursday
                textStart = binding.thursdayStartText
                textEnd = binding.thursdayEndText
                layout = binding.thursdayChipLayout
            }
            getString(R.string.friday_word) -> {
                dayOfWeek = DayOfWeek.FRIDAY
                chip = binding.chipFriday
                textStart = binding.fridayStartText
                textEnd = binding.fridayEndText
                layout = binding.fridayChipLayout
            }
            getString(R.string.saturday_word) -> {
                dayOfWeek = DayOfWeek.SATURDAY
                chip = binding.chipSaturday
                textStart = binding.saturdayStartText
                textEnd = binding.saturdayEndText
                layout = binding.saturdayChipLayout
            }
            getString(R.string.sunday_word) -> {
                dayOfWeek = DayOfWeek.SUNDAY
                chip = binding.chipSunday
                textStart = binding.sundayStartText
                textEnd = binding.sundayEndText
                layout = binding.sundayChipLayout
            } else -> {
                dayOfWeek = DayOfWeek.MONDAY
                chip = binding.chipMonday
                textStart = binding.mondayStartText
                textEnd = binding.mondayEndText
                layout = binding.mondayChipLayout
            }
        }

        return ChipComponent(
            dayOfWeek = dayOfWeek,
            chip = chip,
            textStart = textStart,
            textEnd = textEnd,
            layout = layout
        )
    }

    private fun setupChipListeners() {
        dayChips.forEach { selectedChip ->
            selectedChip.setOnClickListener {
                val selectedDay = selectedChip.text.toString()

                val chipComponent = getChipByStringOfDay(selectedDay)

                val textStart = chipComponent.textStart
                val textEnd = chipComponent.textEnd
                val dayOfWeek = chipComponent.dayOfWeek
                val layout = chipComponent.layout

                if (!selectedChip.isChecked) {
                    val scheduleId = chipIdMap[selectedChip]

                    if (scheduleId != null && authHeader != null) {
                        schedulesViewModel.deleteSchedule(scheduleId, authHeader!!)
                        layout.visibility = View.GONE
                        listenScheduleDeleted(selectedChip, layout)
                    }else {
                        Log.e("AddEditSchedulesFragment", "No se ha podido eliminar el horario, scheduleId ($scheduleId) o authHeader ($authHeader) nulos")
                    }

                } else {
                    selectedChip.isChecked = false
                    showTimePicker({ startHour ->
                        textStart.text =
                            "${getString(R.string.start_word)}: ${TextUtils.formatTime(startHour)}"

                        showTimePicker({ endHour ->

                            textEnd.text =
                                "${getString(R.string.end_word)}: ${TextUtils.formatTime(endHour)}"

                            layout.visibility = View.VISIBLE
                            selectedChip.isChecked = true

                            schedules.removeIf { it.dayOfWeek == dayOfWeek }

                            val activity = requireActivity() as AppCompatActivity
                            val idService = activity.intent.getStringExtra(EXTRA_SERVICE_ID)

                            val scheduleId = chipIdMap[selectedChip]

                            if(scheduleId == null) {
                                val schedule = CreateServiceScheduleRequest(
                                    dayOfWeek,
                                    startHour.toString(),
                                    endHour.toString()
                                )

                                if (authHeader != null && idService != null) {
                                    serviceViewModel.createServiceSchedule(
                                        authHeader!!,
                                        idService,
                                        schedule
                                    )
                                    listenScheduleCreated(selectedChip, layout)
                                }
                            } else {
                                val patchScheduleRequest = PatchScheduleRequest(
                                    dayOfWeek.value,
                                    startHour.toString(),
                                    endHour.toString()
                                )

                                if (authHeader != null && idService != null) {
                                    schedulesViewModel.updateSchedule(
                                        scheduleId, authHeader!!, patchScheduleRequest
                                    )
                                    listenScheduleUpdated(selectedChip, layout)
                                }
                            }
                        }, getString(R.string.end_hour))
                    }, getString(R.string.start_hour))
                }
            }
        }
    }

    private fun listenScheduleCreated(chip: Chip, chipLayout: LinearLayout) {
        serviceViewModel.serviceScheduleCreated.observe(viewLifecycleOwner) { response ->
            if(response == null) {
                return@observe
            }

            if (!Verifier.verifySuccessfulResponse(
                    response,
                    requireContext(),
                    positiveActionText = getString(R.string.dialog_ok)
                )
            ) {
                chip.isChecked = false
                chipLayout.visibility = View.GONE

                serviceViewModel.serviceScheduleCreated.removeObservers(viewLifecycleOwner)
            } else {
                val createdSchedule = response.body()!!
                Log.i("AddEditSchedulesFragment", "Horario creado: $createdSchedule")

                chipIdMap[chip] = createdSchedule.scheduleId
                serviceViewModel.serviceScheduleCreated.removeObservers(viewLifecycleOwner)
            }
        }
    }

    private fun listenScheduleDeleted(chip: Chip, chipLayout: LinearLayout) {
        schedulesViewModel.scheduleDeleted.observe(viewLifecycleOwner) { response ->
            if(response == null)  {
                return@observe
            }
            if (!Verifier.verifySuccessfulResponse(
                    response,
                    requireContext(),
                    positiveActionText = getString(R.string.dialog_ok)
                )
            ) {
                chip.isChecked = true
                chipLayout.visibility = View.VISIBLE
                Log.e("Schedules", "Error al eliminar: ${response.code()}")

                schedulesViewModel.scheduleDeleted.removeObservers(viewLifecycleOwner)
            }

            chipIdMap.remove(chip)

            schedulesViewModel.scheduleDeleted.removeObservers(viewLifecycleOwner)
            Log.i("AddEditSchedulesFragment", "Horario eliminado: ${response.code()}")
        }
    }

    private fun listenScheduleUpdated(chip: Chip, chipLayout: LinearLayout) {
        schedulesViewModel.scheduleUpdated.observe(viewLifecycleOwner) { response ->
            if(response == null) {
                return@observe
            }
            if(!Verifier.verifySuccessfulResponse(
                    response,
                    requireContext(),
                    positiveActionText = getString(R.string.dialog_ok)
                )
            ) {
                chip.isChecked = false
                chipLayout.visibility = View.GONE

                schedulesViewModel.scheduleUpdated.removeObservers(viewLifecycleOwner)
            } else {
                val updatedSchedule = response.body()!!

                Log.i("AddEditSchedulesFragment", "Horario actualizado: $updatedSchedule")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}