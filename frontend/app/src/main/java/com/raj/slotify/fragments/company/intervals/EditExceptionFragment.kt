package com.raj.slotify.fragments.company.intervals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.raj.slotify.R
import com.raj.slotify.databinding.FragmentEditExceptionBinding
import com.raj.slotify.dtos.company.IntervalSummary
import com.raj.slotify.dtos.company.UpdateIntervalRequest
import com.raj.slotify.models.TextModel
import com.raj.slotify.viewModels.apiRest.IntervalViewModel
import com.raj.slotify.viewModels.frontend.MainViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class EditExceptionFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val userDataViewModel: UserDataViewModel by activityViewModels()
    private val intervalViewModel: IntervalViewModel by activityViewModels()

    private lateinit var binding: FragmentEditExceptionBinding

    private var interval: IntervalSummary? = null

    private var selectedStart: LocalDateTime? = null
    private var selectedEnd: LocalDateTime? = null

    private val displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainViewModel.setTitle(TextModel(R.string.edit_exception))
        binding = FragmentEditExceptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        interval = intervalViewModel.selectedInterval.value
        loadIntervalData()
        setupNumberPicker()
        updatePeriodTexts()

        binding.discardChangesLink.setOnClickListener {
            view.findNavController().popBackStack()
        }

        binding.selectPeriodButton.setOnClickListener { showDateRangePicker() }

        binding.deleteButton.setOnClickListener { confirmDelete() }

        binding.confirmButton.setOnClickListener { onConfirm() }

        intervalViewModel.intervalUpdated.observe(viewLifecycleOwner) { response ->
            if (response == null) return@observe
            if (response.isSuccessful) {
                view.findNavController().popBackStack()
            } else {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.validation_error_title))
                    .setMessage("${response.code()}: ${response.message()}")
                    .setPositiveButton(getString(R.string.dialog_ok), null)
                    .show()
            }
        }

        intervalViewModel.intervalDeletedResponse.observe(viewLifecycleOwner) { response ->
            if (response == null) return@observe
            if (response.isSuccessful) {
                view.findNavController().popBackStack()
            } else {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.validation_error_title))
                    .setMessage("${response.code()}: ${response.message()}")
                    .setPositiveButton(getString(R.string.dialog_ok), null)
                    .show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        intervalViewModel.clearSelectedInterval()
    }

    private fun loadIntervalData() {
        val current = interval ?: return
        selectedStart = current.startDateTime
        selectedEnd = current.endDateTime
    }

    private fun setupNumberPicker() {
        binding.concurrentServicesPicker.minValue = 0
        binding.concurrentServicesPicker.maxValue = 20
        binding.concurrentServicesPicker.value = interval?.maxConcurrentServices ?: 1
        binding.concurrentServicesPicker.wrapSelectorWheel = false
    }

    private fun showDateRangePicker() {
        val picker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText(getString(R.string.establish_a_period))
            .build()

        picker.addOnPositiveButtonClickListener { selection ->
            val startMillis = selection.first ?: return@addOnPositiveButtonClickListener
            val endMillis = selection.second ?: return@addOnPositiveButtonClickListener

            selectedStart = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(startMillis), ZoneOffset.UTC
            ).withHour(0).withMinute(0).withSecond(0)

            selectedEnd = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(endMillis), ZoneOffset.UTC
            ).withHour(23).withMinute(59).withSecond(59)

            updatePeriodTexts()
        }

        picker.show(parentFragmentManager, "date_range_picker")
    }

    private fun updatePeriodTexts() {
        val start = selectedStart
        val end = selectedEnd

        if (start == null || end == null) {
            binding.periodStartText.text = "${getString(R.string.start_word)}: —"
            binding.periodEndText.text = "${getString(R.string.end_word)}: —"
        } else {
            val startDay = start.dayOfWeek
                .getDisplayName(TextStyle.FULL, Locale.getDefault())
                .replaceFirstChar { it.uppercase() }
            val endDay = end.dayOfWeek
                .getDisplayName(TextStyle.FULL, Locale.getDefault())
                .replaceFirstChar { it.uppercase() }

            binding.periodStartText.text = "${getString(R.string.start_word)}: $startDay ${start.format(displayFormatter)}"
            binding.periodEndText.text = "${getString(R.string.end_word)}: $endDay ${end.format(displayFormatter)}"
        }
    }

    private fun confirmDelete() {
        val intervalId = interval?.intervalId ?: return
        val token = userDataViewModel.userToken.value?.accessToken ?: return

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_exception_title))
            .setMessage(getString(R.string.delete_exception_confirmation))
            .setNegativeButton(getString(R.string.negation_word), null)
            .setPositiveButton(getString(R.string.yes_word)) { _, _ ->
                intervalViewModel.deleteInterval("Bearer $token", intervalId)
            }
            .show()
    }

    private fun onConfirm() {
        val start = selectedStart
        val end = selectedEnd
        val intervalId = interval?.intervalId

        if (start == null || end == null || intervalId == null) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.validation_error_title))
                .setMessage(getString(R.string.establish_a_period))
                .setPositiveButton(getString(R.string.dialog_ok), null)
                .show()
            return
        }

        val token = userDataViewModel.userToken.value?.accessToken ?: return
        val concurrentServices = binding.concurrentServicesPicker.value

        intervalViewModel.updateInterval(
            "Bearer $token",
            intervalId,
            UpdateIntervalRequest(
                startDateTime = start,
                endDateTime = end,
                maxConcurrentService = concurrentServices
            )
        )
    }
}
