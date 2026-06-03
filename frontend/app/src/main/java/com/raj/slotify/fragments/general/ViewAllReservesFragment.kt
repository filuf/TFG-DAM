package com.raj.slotify.fragments.general

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raj.slotify.R
import com.raj.slotify.activities.client.ClientReserveDetailsActivity
import com.raj.slotify.activities.client.CompanyReserveDetailsActivity
import com.raj.slotify.activities.client.MakeReserveActivity
import com.raj.slotify.adapters.ReservesListCustomAdapter
import com.raj.slotify.databinding.FragmentViewAllReservesBinding
import com.raj.slotify.dtos.reserves.CompanyReserveSummary
import com.raj.slotify.dtos.reserves.ReserveSummary
import com.raj.slotify.dtos.reserves.UserReserveSummary
import com.raj.slotify.viewModels.apiRest.ReservesViewModel
import com.raj.slotify.viewModels.frontend.ClientReservesViewModel
import com.raj.slotify.viewModels.frontend.LayoutViewModel
import com.raj.slotify.viewModels.frontend.MainViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel

class ViewAllReservesFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val layoutViewModel: LayoutViewModel by activityViewModels()
    private val userDataViewModel: UserDataViewModel by activityViewModels()
    private val clientReservesViewModel: ClientReservesViewModel by activityViewModels()
    private val reservesViewModel: ReservesViewModel by activityViewModels()

    private lateinit var binding: FragmentViewAllReservesBinding
    private lateinit var returnPageButton: Button
    private lateinit var advancePageButton: Button
    private var totalPages: Int = 0
    private var actualPage: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewAllReservesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setMakeReserveButton()
        setPaginationLogic()

        // LOAD RESERVES
        userDataViewModel.userToken.observe(viewLifecycleOwner) { token ->
            val accessToken = token.accessToken
            reservesViewModel.getReserves("Bearer $accessToken", 0, null, null, null)
        }

        // SET PAGE LAYOUT CARD
        val pageTextView: TextView = binding.textPage

        // RECYCLER VIEW
        val customAdapter = configRecyclerView(view)
        val searchReserveText = binding.searchReservesText

        searchReserveText.doOnTextChanged { text, _, _, _ ->
            val reserves: MutableList<ReserveSummary> = clientReservesViewModel.reserves.value ?: mutableListOf()

            if (text.isNullOrEmpty()) {
                customAdapter.setItems(reserves)
            } else {
                val filteredReserves: MutableList<ReserveSummary> = reserves
                    .filter { reserve -> reserve.serviceName.lowercase().contains(text.toString().lowercase().trim()) }
                    .toMutableList()
                customAdapter.setItems(filteredReserves)
            }
        }
        observeReserves(pageTextView, customAdapter)
    }

    private fun observeReserves(
        pageTextView: TextView,
        customAdapter: ReservesListCustomAdapter
    ) {
        // MANAGE POSITION
        fun isFirst(actualPage: Int): Boolean {
            return actualPage == 0
        }
        fun isLast(actualPage: Int, totalPages: Int): Boolean {
            if (totalPages == 0)
                return true

            return actualPage == totalPages - 1
        }

        reservesViewModel.reservesSummary.observe(viewLifecycleOwner) { response ->
            // DISCARD UNSUCCESSFULLY CASES
            if (response == null)
                return@observe

            if (!response.isSuccessful) {
                Log.e("ViewAllReserves", "Error cargando reservas: ${response.code()}")
                return@observe
            }
            if (response.body() == null)
                return@observe

            val responseBody = response.body()!!
            val reservesSummary = responseBody.content

            // UPDATE CLIENT RESERVES VIEW MODEL
            clientReservesViewModel.setReserves(reservesSummary.toMutableList())

            // PAGE LOGIC
            if (totalPages == 0 && responseBody.totalPages != 0)
                totalPages = responseBody.totalPages

            actualPage = responseBody.number

            val pageText =
                "${getString(R.string.page_word)} ${actualPage + 1} ${getString(R.string.of_word)} $totalPages"
            pageTextView.text = pageText

            // PAGE BUTTON ENABLED BEHAVIOUR
            returnPageButton.isEnabled = !isFirst(actualPage)
            advancePageButton.isEnabled = !isLast(actualPage, totalPages)

            customAdapter.setItems(reservesSummary)
            val numberOfReserves = responseBody.totalElements.toInt()

            // REMAINING TEXT
            binding.numberReservesRemainingText.text = numberOfReserves.toString()
            val reserveText =
                if (numberOfReserves == 1) "${getString(R.string.reserve)} ${getString(R.string.pending_word)}"
                else "${getString(R.string.reserves)} ${getString(R.string.pending_plural)}"

            binding.remaingText.text = reserveText
        }

        // OBSERVER FOR SEARCH
        clientReservesViewModel.reserves.observe(viewLifecycleOwner) { reserves ->
            customAdapter.setItems(reserves)
        }
    }

    private fun setPaginationLogic() {
        returnPageButton = binding.returnPageButton
        advancePageButton = binding.advancePageButton

        returnPageButton.isEnabled = false
        advancePageButton.isEnabled = false

        // SET PAGE BUTTONS
        val accessToken = userDataViewModel.userToken.value?.accessToken ?: ""

        returnPageButton.setOnClickListener {
            val destinyPage = actualPage - 1
            reservesViewModel.getReserves("Bearer $accessToken", destinyPage, null, null, null)
        }
        advancePageButton.setOnClickListener {
            val destinyPage = actualPage + 1
            reservesViewModel.getReserves("Bearer $accessToken", destinyPage, null, null, null)
        }
    }

    private fun configRecyclerView(view: View): ReservesListCustomAdapter {
        val recyclerView: RecyclerView = binding.viewAllReservesRecycler
        val customAdapter = ReservesListCustomAdapter(
            clientReservesViewModel.reserves.value ?: mutableListOf()
        ) { reserve: ReserveSummary ->
            if (reserve is UserReserveSummary) {
                val intent = Intent(requireActivity(), ClientReserveDetailsActivity::class.java)
                intent.putExtra("EXTRA_RESERVE", reserve)
                startActivity(intent)
            } else if (reserve is CompanyReserveSummary) {
                val intent = Intent(requireActivity(), CompanyReserveDetailsActivity::class.java)
                intent.putExtra("EXTRA_RESERVE", reserve)
                startActivity(intent)
            }
        }

        recyclerView.adapter = customAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        return customAdapter
    }

    private fun setMakeReserveButton() {
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
    }

}