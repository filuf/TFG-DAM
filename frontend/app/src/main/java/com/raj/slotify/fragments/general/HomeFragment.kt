package com.raj.slotify.fragments.general

import android.content.Intent
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
import com.raj.slotify.activities.LogInActivity
import com.raj.slotify.activities.ReserveDetailsActivity
import com.raj.slotify.adapters.ReservesListCustomAdapter
import com.raj.slotify.databinding.FragmentHomeBinding
import com.raj.slotify.dtos.reserves.ReserveSummary
import com.raj.slotify.models.TextModel
import com.raj.slotify.tools.Navigation
import com.raj.slotify.viewModels.apiRest.ReservesViewModel
import com.raj.slotify.viewModels.frontend.ClientReservesViewModel
import com.raj.slotify.viewModels.frontend.LayoutViewModel
import com.raj.slotify.viewModels.frontend.MainViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel

class HomeFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val userDataViewModel: UserDataViewModel by activityViewModels()
    private val clientReservesViewModel: ClientReservesViewModel by activityViewModels()
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
        viewModel.setTitle(TextModel(R.string.welcome_greeting))
        layoutViewModel.setSecondTextVisibility(View.VISIBLE)

        layoutViewModel.setCenterIconTitle(true)

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homeProgressBar.visibility = View.VISIBLE
        binding.homeReservesLayout.visibility = View.GONE

        // OBSERVE USER TOKEN AND GET RESERVES
        userDataViewModel.userToken.observe(viewLifecycleOwner) { token ->
            val accessToken = token.accessToken
            reservesViewModel.getReserves("Bearer $accessToken", 0, null, null, "PRESENT")
        }

        val intervalButton = binding.createIntervalButton
        userDataViewModel.userType.observe(viewLifecycleOwner) { userType ->
            if (userType.equals("USER")) {
                intervalButton.visibility = View.GONE
            } else {
                intervalButton.visibility = View.VISIBLE
            }
        }
        intervalButton.setOnClickListener {
            // TODO: NAVEGAR A LA PÁGINA DE INTERVALOS
        }

        userDataViewModel.name.observe(viewLifecycleOwner) { name ->
            viewModel.setSecondTitle(name)
        }

        val seeMoreButton = binding.seeMoreLayout
        seeMoreButton.setOnClickListener {
            Navigation.navigateSafely(view.findNavController(), R.id.action_homeFragment_to_viewAllReservesFragment)
        }

        val recyclerView: RecyclerView = binding.recyclerHome
        val customAdapter = ReservesListCustomAdapter(
            clientReservesViewModel.reserves.value ?: mutableListOf()
        ) { reserve: ReserveSummary ->
            val intent = Intent(requireActivity(), ReserveDetailsActivity::class.java)
            intent.putExtra("EXTRA_RESERVE", reserve)
            startActivity(intent)
        }

        recyclerView.adapter = customAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        reservesViewModel.reservesSummary.observe(viewLifecycleOwner) { response ->
            try {
                if (response != null) {
                    if (response.isSuccessful && response.body() != null) {
                        val reservesSummary = response.body()!!.content

                        val totalReservesCount = response.body()!!.totalElements.toInt()
                        val canceledReservesCount = reservesSummary.count { reserve -> reserve.isCanceled }

                        val numberOfReserves = totalReservesCount - canceledReservesCount

                        // Set visibility of see more reserves button
                        if (numberOfReserves == 0) {
                            binding.seeMoreLayout.visibility = View.GONE
                        } else {
                            binding.seeMoreLayout.visibility = View.VISIBLE
                        }

                        binding.numberReservesRemainingText.text = numberOfReserves.toString()
                        val reserveText = if (numberOfReserves == 1) "${getString(R.string.reserve)} ${getString(R.string.pending_word)}" else "${getString(R.string.reserves)} ${getString(R.string.pending_plural)}"

                        binding.remaingText.text = reserveText

                        val noCanceledReserves = reservesSummary
                            .filter { reserve -> !reserve.isCanceled }
                            .toMutableList()

                        customAdapter.setItems(noCanceledReserves.take(2))

                        binding.homeProgressBar.visibility = View.GONE
                        binding.homeReservesLayout.visibility = View.VISIBLE

                    } else {
                        if (response.code() == 401 && response.message().equals("Unauthorized")) {
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle(getString(R.string.sesion_expired_title))
                                .setMessage(getString(R.string.sesion_expired_explication))
                                .setPositiveButton(getString(R.string.dialog_ok)) { _, _ ->
                                    val intent = Intent(requireActivity(), LogInActivity::class.java)
                                    startActivity(intent)
                                }.show()
                        }
                        Log.e("ERROR", "Error al obtener reservas: ${response.code()}: ${response.body()}")
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