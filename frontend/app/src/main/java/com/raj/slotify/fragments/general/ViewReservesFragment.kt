package com.raj.slotify.fragments.general

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.raj.slotify.R
import com.raj.slotify.activities.client.MakeReserveActivity
import com.raj.slotify.adapters.ReservesPagerAdapter
import com.raj.slotify.databinding.FragmentViewReservesBinding
import com.raj.slotify.dtos.reserves.ReserveSummary
import com.raj.slotify.viewModels.apiRest.ReservesViewModel
import com.raj.slotify.viewModels.frontend.ClientReservesViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel

class ViewReservesFragment : Fragment() {

    private val userDataViewModel: UserDataViewModel by activityViewModels()
    private val clientReservesViewModel: ClientReservesViewModel by activityViewModels()
    private val reservesViewModel: ReservesViewModel by activityViewModels()

    private lateinit var binding: FragmentViewReservesBinding
    private lateinit var authHeader: String
    private lateinit var allReserves: MutableList<ReserveSummary>
    private var fetchType: String = "PRESENT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewReservesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeReserves()
        setMakeReserveButton()
        configTabBehaviour()

        val searchReserveText = binding.searchReservesText

        searchReserveText.doOnTextChanged { text, _, _, _ ->
            if (!::allReserves.isInitialized) return@doOnTextChanged

            if (text.isNullOrEmpty()) {
                clientReservesViewModel.setReserves(allReserves)
            } else {
                val filteredReserves: MutableList<ReserveSummary> = allReserves
                    .filter { reserve -> reserve.serviceName.lowercase().contains(text.toString().lowercase().trim()) }
                    .toMutableList()
                clientReservesViewModel.setReserves(filteredReserves)
            }
        }
    }

    private fun observeReserves() {
        binding.viewReservesProgressBar.visibility = View.VISIBLE
        binding.viewPageator.visibility = View.GONE

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

            val reserveSummary = response.body()!!.content

            allReserves = reserveSummary.toMutableList()
            clientReservesViewModel.setReserves(allReserves)
            clientReservesViewModel.setReservesPage(response.body()!!)

            binding.viewReservesProgressBar.visibility = View.GONE
            binding.viewPageator.visibility = View.VISIBLE
        }
    }

    private fun configTabBehaviour() {
        val tabLayout = binding.reservesTabLayout

        val elements = listOf(
            getString(R.string.reserve_pending_word),
            getString(R.string.reserve_all_word),
            getString(R.string.reserves_past_word)
        )

        val adapter = ReservesPagerAdapter(requireActivity().supportFragmentManager, requireActivity().lifecycle)
        binding.viewPageator.adapter = adapter

        TabLayoutMediator(tabLayout, binding.viewPageator) { tab, position ->
            tab.text = elements[position]

            fetchType = "PRESENT"
            clientReservesViewModel.setFetchType(fetchType)

            getReservesByPage(0, fetchType)
        }.attach()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab?.position ?: 0

                fetchType = when (position) {
                    0 -> "PRESENT"
                    1 -> "ALL"
                    2 -> "PAST"
                    else -> "PRESENT"
                }
                clientReservesViewModel.setFetchType(fetchType)

                Log.i("ViewReservesFragment", "Tab seleccionado: $position - Tipo: $fetchType")
                getReservesByPage(0, fetchType)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {
                onTabSelected(tab)
            }
        })
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