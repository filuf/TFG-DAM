package com.raj.slotify.fragments.general

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.raj.slotify.R
import com.raj.slotify.adapters.ReservesListCustomAdapter
import com.raj.slotify.databinding.FragmentHomeBinding
import com.raj.slotify.dtos.reserves.ReserveSummary
import com.raj.slotify.models.ReserveRecyclerItem
import com.raj.slotify.viewModels.apiRest.ReservesViewModel
import com.raj.slotify.viewModels.frontend.LayoutViewModel
import com.raj.slotify.viewModels.frontend.MainViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel
import java.time.LocalDateTime

class HomeFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val userDataViewModel: UserDataViewModel by activityViewModels()
    private val layoutViewModel: LayoutViewModel by activityViewModels()

    private val reservesViewModel: ReservesViewModel by activityViewModels()

    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.setNewTitle(R.string.welcome_greeting)
        viewModel.setSecondTitle(userDataViewModel.name.value?:"unknown user")
        layoutViewModel.setSecondTextVisibility(View.VISIBLE)

        layoutViewModel.setCenterIconTitle(true)

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = binding.recyclerHome
        val customAdapter = ReservesListCustomAdapter(mutableListOf()) { reserve: ReserveRecyclerItem ->
            val userToken = "Bearer ${userDataViewModel.userToken.value?.accessToken?:""}"
            reservesViewModel.getReserveById(reserve.id, userToken)

            view.findNavController().navigate(R.id.action_homeFragment_to_reserveDetailsFragment)
        }

        recyclerView.adapter = customAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        reservesViewModel.reservesSummary.observe(viewLifecycleOwner) { response ->
            try {
                if (response != null) {
                    if (response.isSuccessful && response.body() != null) {
                        val reservesSummary = response.body()!!.content
                        val reservesSummarySelection: MutableList<ReserveSummary> =
                            reservesSummary.take(2).toMutableList()

                        fun calculateDate(
                            startTime: LocalDateTime,
                            endTime: LocalDateTime
                        ): List<String> {
                            val listToReturn: ArrayList<String> = arrayListOf()

                            listToReturn.add("${startTime.hour}:${startTime.minute} - ${endTime.hour}:${endTime.minute}")
                            listToReturn.add("${startTime.dayOfMonth}/${startTime.monthValue}/${startTime.year}")
                            return listToReturn
                        }

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
                                remainingDays > 0 -> " $remainingDays ${
                                    if (remainingDays > 1) getString(
                                        R.string.days_word
                                    ) else getString(R.string.day_word)
                                } ${getString(R.string.left_after)} "

                                remainingHours > 0 -> " $remainingHours ${
                                    if (remainingHours > 1) getString(
                                        R.string.hours_word
                                    ) else getString(R.string.hour_word)
                                } ${getString(R.string.left_after)} "

                                else -> " $remainingMinutes ${
                                    if (remainingMinutes > 1) getString(R.string.minutes_word) else getString(
                                        R.string.minute_word
                                    )
                                } ${getString(R.string.left_after)} "
                            }
                        }

                        val recyclerItems: MutableList<ReserveRecyclerItem> =
                            reservesSummarySelection
                                .map { reserveSummary ->
                                    ReserveRecyclerItem(
                                        reserveSummary.reserveId,
                                        reserveSummary.serviceName,
                                        calculateDate(
                                            reserveSummary.startDateTime,
                                            reserveSummary.endDateTime
                                        )[1],
                                        calculateDate(
                                            reserveSummary.startDateTime,
                                            reserveSummary.endDateTime
                                        )[0],
                                        calculateRemainingText(reserveSummary.startDateTime)
                                    )
                                }
                                .toMutableList()

                        customAdapter.setItems(recyclerItems)
                    }
                }
            } catch (e: Exception) {
                Log.e("ERROR", "Error al obtener las reservas: ${e.message}")
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("ERROR al obtener las reservas")
                    .setMessage(e.message)
                    .setPositiveButton("OK", null)
                    .show()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutViewModel.setCenterIconTitle(false)
    }

}