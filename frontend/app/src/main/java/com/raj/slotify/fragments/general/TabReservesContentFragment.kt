package com.raj.slotify.fragments.general

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raj.slotify.R
import com.raj.slotify.activities.ReserveDetailsActivity
import com.raj.slotify.adapters.ReservesListCustomAdapter
import com.raj.slotify.databinding.FragmentTabReservesContentBinding
import com.raj.slotify.dtos.reserves.ReserveSummary
import com.raj.slotify.viewModels.apiRest.ReservesViewModel
import com.raj.slotify.viewModels.frontend.ClientReservesViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel

class TabReservesContentFragment : Fragment() {
    private val clientReservesViewModel: ClientReservesViewModel by activityViewModels()
    private val userDataViewModel: UserDataViewModel by activityViewModels()
    private val reservesViewModel: ReservesViewModel by activityViewModels()

    private var reserves: MutableList<ReserveSummary> = mutableListOf()
    private var nonCanceledReserves: MutableList<ReserveSummary> = mutableListOf()
    private lateinit var binding: FragmentTabReservesContentBinding
    private lateinit var returnPageButton: Button
    private lateinit var advancePageButton: Button
    private lateinit var authHeader: String
    private var totalPages: Int = 0
    private var actualPage: Int = 1
    private var fetchType: String = "PRESENT"
    private var totalReserves = 0
    private var canceledReserves = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTabReservesContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setPaginationLogic()

        val adapter = configRecyclerView()
        observeReservesPage(adapter)

        val canceledButton = binding.canceledAppointmentSwitch
        canceledButton.isChecked = false

        observeReserves(canceledButton, adapter)

        switchCanceledReservesHiding(canceledButton, adapter)
        setCanceledButtonBehaviour(canceledButton, adapter)
    }

    private fun switchCanceledReservesHiding(canceledButton: Switch, adapter: ReservesListCustomAdapter) {

        var numberOfReserves: Int

        if (!canceledButton.isChecked) {
            numberOfReserves = totalReserves - canceledReserves
            configReservesCountText(numberOfReserves, fetchType)

            adapter.setItems(nonCanceledReserves)
        } else {
            adapter.setItems(reserves)
            numberOfReserves = totalReserves
        }

        configReservesCountText(numberOfReserves, fetchType)
    }

    private fun observeReserves(canceledButton: Switch, adapter: ReservesListCustomAdapter) {
        clientReservesViewModel.reserves.observe(viewLifecycleOwner) { reservesSummary ->
            reserves = reservesSummary

            // SHOW NOT CANCELED
            nonCanceledReserves = reservesSummary
                .filter { reserve -> !reserve.isCanceled }
                .toMutableList()

            val canceledReservesCount = reservesSummary.count { reserve -> reserve.isCanceled }
            canceledReserves = canceledReservesCount

            val canceledButtonChecked = canceledButton.isChecked
            if (canceledButtonChecked) {
                adapter.setItems(reserves)
            } else {
                adapter.setItems(nonCanceledReserves)
            }
        }
    }

    private fun setCanceledButtonBehaviour(canceledButton: Switch, adapter: ReservesListCustomAdapter) {

        canceledButton.setOnCheckedChangeListener { _, _ ->
            switchCanceledReservesHiding(canceledButton, adapter)
        }
    }

    private fun showCanceledReservesByFetchType(fetchType: String) {
        binding.showCanceledCard.visibility = if (fetchType == "ALL") View.VISIBLE else View.GONE
    }

    private fun observeReservesPage(adapter: ReservesListCustomAdapter) {
        // MANAGE POSITION
        fun isFirst(actualPage: Int): Boolean {
            return actualPage == 0
        }
        fun isLast(actualPage: Int, totalPages: Int): Boolean {
            if (totalPages == 0)
                return true

            return actualPage == totalPages - 1
        }

        clientReservesViewModel.reservesPage.observe(viewLifecycleOwner) { reservePage ->
            totalReserves = reservePage.totalElements.toInt()

            clientReservesViewModel.fetchType.let { fetchType ->
                if (fetchType.value != null) {
                    this.fetchType = fetchType.value!!
                    val numberOfReserves = totalReserves - canceledReserves

                    configReservesCountText(numberOfReserves, fetchType.value!!)
                    switchCanceledReservesHiding(binding.canceledAppointmentSwitch, adapter)

                    showCanceledReservesByFetchType(fetchType.value!!)
                }
            }

            // PAGE LOGIC
            if (totalPages == 0 && reservePage.totalPages != 0)
                totalPages = reservePage.totalPages

            actualPage = reservePage.number

            val pageText =
                "${getString(R.string.page_word)} ${actualPage + 1} ${getString(R.string.of_word)} $totalPages"
            binding.textPage.text = pageText

            // PAGE BUTTON ENABLED BEHAVIOUR
            returnPageButton.isEnabled = !isFirst(actualPage)
            advancePageButton.isEnabled = !isLast(actualPage, totalPages)
        }
    }

    private fun getReservesByPage(page: Int=0, fetchType: String = this.fetchType) {
        if (!::authHeader.isInitialized) {
            userDataViewModel.userToken.observe(viewLifecycleOwner) { token ->
                val accessToken = token.accessToken
                authHeader = "Bearer $accessToken"

                reservesViewModel.getReserves(authHeader, page, null, null, fetchType)
            }
        } else {
            reservesViewModel.getReserves(authHeader, page, null, null, fetchType)
        }
    }

    private fun setPaginationLogic() {
        returnPageButton = binding.returnPageButton
        advancePageButton = binding.advancePageButton

        returnPageButton.isEnabled = false
        advancePageButton.isEnabled = false

        // SET PAGE BUTTONS
        returnPageButton.setOnClickListener {
            val destinyPage = actualPage - 1
            getReservesByPage(destinyPage)
        }
        advancePageButton.setOnClickListener {
            val destinyPage = actualPage + 1
            getReservesByPage(destinyPage)
        }
    }

    private fun configReservesCountText(numberOfReserves: Int, fetchType: String) {
        // REMAINING TEXT
        binding.numberReservesRemainingText.text = numberOfReserves.toString()
        binding.youHaveText.text = getString(R.string.you_have)

        val isOnlyOneReserve = numberOfReserves == 1

        val reserveWord =
                if (isOnlyOneReserve) getString(R.string.reserve)
                else getString(R.string.reserves)

        val youHaveCompletedWords = getString(R.string.you_have) + " " +
                if (isOnlyOneReserve) getString(R.string.completed_word)
                else getString(R.string.completeds_word)

        val presentText = "$reserveWord ${
                if (isOnlyOneReserve) getString(R.string.pending_word)
                else getString(R.string.pending_plural)
        }"

        var textAfterNumber: String

        when(fetchType) {
            "PRESENT" -> {
                textAfterNumber = presentText
            }
            "ALL" -> {
                textAfterNumber = "$reserveWord ${getString(R.string.in_total_word)}"
            }
            "PAST" -> {
                binding.youHaveText.text = youHaveCompletedWords
                textAfterNumber = reserveWord
            }
            else -> {
                textAfterNumber = presentText
            }
        }

        binding.remaingText.text = textAfterNumber
    }

    private fun configRecyclerView(): ReservesListCustomAdapter {
        val recyclerView: RecyclerView = binding.viewAllReservesRecycler
        val customAdapter = ReservesListCustomAdapter(
            reserves
        ) { reserve: ReserveSummary ->
            val intent = Intent(requireActivity(), ReserveDetailsActivity::class.java)
            intent.putExtra("EXTRA_RESERVE", reserve)
            startActivity(intent)
        }

        recyclerView.adapter = customAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        return customAdapter
    }

}