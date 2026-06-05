package com.raj.slotify.fragments.general

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.applandeo.materialcalendarview.CalendarDay
import com.raj.slotify.R
import com.raj.slotify.databinding.FragmentCalendarBinding
import com.raj.slotify.viewModels.apiRest.ReservesViewModel
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.raj.slotify.activities.ReserveDetailsActivity
import com.raj.slotify.activities.client.MakeReserveActivity
import com.raj.slotify.dtos.reserves.ReserveSummary
import com.raj.slotify.viewModels.frontend.ClientReservesViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel

class CalendarFragment : Fragment(), OnCalendarDayClickListener {
    private val userDataViewModel: UserDataViewModel by activityViewModels()
    private val clientReservesViewModel: ClientReservesViewModel by activityViewModels()

    private val reservesViewModel: ReservesViewModel by activityViewModels()

    private lateinit var binding: FragmentCalendarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun selectDateInCalendar(dateTime: LocalDateTime) {
        val millis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        binding.calendarView.setDate(Date(millis))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val makeReserveButton = binding.makeReserveButton

        userDataViewModel.userType.observe(viewLifecycleOwner) { userType ->
            if (userType.equals("USER")) {
                makeReserveButton.visibility = View.VISIBLE
            } else {
                makeReserveButton.visibility = View.GONE
            }
        }

        makeReserveButton.setOnClickListener {
            val intent = Intent(requireActivity(), MakeReserveActivity::class.java)
            startActivity(intent)
        }

        binding.calendarView.setOnCalendarDayClickListener(this)

        // ADD TODAY DAY
        val today = Calendar.getInstance()
        val todayCalendar = CalendarDay(today)
        todayCalendar.backgroundResource = R.drawable.calendar_day_background

        val calendarDays: MutableList<CalendarDay> = ArrayList<CalendarDay>()
        calendarDays.add(todayCalendar)

        reservesViewModel.reservesSummary.observe(viewLifecycleOwner) { response ->
            if (response == null)
                return@observe
            if (!response.isSuccessful) {
                return@observe
            }
            val content = response.body()?.content

            if (content == null)
                return@observe

            clientReservesViewModel.setReserves(content.toMutableList())

            for (reserve in content) {
                val startTime = reserve.startDateTime
                val calendar = Calendar.getInstance()

                val timesTamp = startTime.toInstant(ZoneId.systemDefault().rules.getOffset(startTime))

                calendar.timeInMillis = timesTamp.toEpochMilli()
                val calendarDay = CalendarDay(calendar)

                calendarDay.imageResource = R.drawable.sample_circle
                calendarDay.labelColor = R.color.nav_item_color

                calendarDays.add(calendarDay)
            }

            binding.calendarView.setCalendarDays(calendarDays.toList())

            if (content.isNotEmpty()) {
                selectDateInCalendar(content[0].startDateTime)
            }
        }

    }

    private fun showReservesPopup(reserves: List<ReserveSummary>) {
        val items = reserves.map { reserve ->
            "${reserve.serviceName} (${reserve.startDateTime.toLocalTime()})"
        }.toTypedArray()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.daily_reserves_title)
            .setItems(items) { _, which ->
                val reserve = reserves[which]

                val intent = Intent(requireActivity(), ReserveDetailsActivity::class.java)
                intent.putExtra("EXTRA_RESERVE", reserve)
                startActivity(intent)

            }
            .setNegativeButton(R.string.dialog_close, null)
            .show()
    }

    override fun onClick(calendarDay: CalendarDay) {
        Log.i("Calendar", "Selected date: ${calendarDay.calendar}")
        val calendar = calendarDay.calendar

        val instant = calendar.time.toInstant()

        val selectedDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate()
        val allReserves = clientReservesViewModel.reserves.value ?: mutableListOf()

        val dailyReserves = allReserves.filter {
            it.startDateTime.toLocalDate() == selectedDate
        }

        if (dailyReserves.isNotEmpty()) {
            showReservesPopup(dailyReserves)
        } else {
            Log.i("Calendar", "No hay reservas para el día $selectedDate")
        }
    }

}