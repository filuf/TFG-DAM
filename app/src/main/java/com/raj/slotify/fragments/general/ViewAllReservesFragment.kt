package com.raj.slotify.fragments.general

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.raj.slotify.R
import com.raj.slotify.activities.LogInActivity
import com.raj.slotify.activities.client.MakeReserveActivity
import com.raj.slotify.adapters.ReservesListCustomAdapter
import com.raj.slotify.databinding.FragmentViewAllReservesBinding
import com.raj.slotify.dtos.reserves.ReserveSummary
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

        val makeReserveButton = binding.makeReserveButton
        makeReserveButton.setOnClickListener {
            val intent = Intent(requireActivity(), MakeReserveActivity::class.java)
            startActivity(intent)
        }

        val recyclerView: RecyclerView = binding.viewAllReservesRecycler
        val customAdapter = ReservesListCustomAdapter(clientReservesViewModel.reserves.value?:mutableListOf()) { reserve: ReserveSummary ->
            clientReservesViewModel.setLastReserveSelected(reserve)
            view.findNavController().navigate(R.id.action_viewAllReservesFragment_to_reserveDetailsFragment)
        }

        recyclerView.adapter = customAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        reservesViewModel.reservesSummary.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                if (response.isSuccessful) {
                    val reservesSummary = response.body()?.content

                    customAdapter.setItems(reservesSummary?: mutableListOf())

                    val numberOfReserves = reservesSummary?.size?:0

                    binding.numberReservesRemainingText.text = numberOfReserves.toString()
                    val reserveText = if (numberOfReserves == 1) "${getString(R.string.reserve)} ${getString(R.string.pending_word)}" else "${getString(R.string.reserves)} ${getString(R.string.pending_plural)}"

                    binding.remaingText.text = reserveText

                } else {
                    Log.e("ViewAllReserves", "Error cargando reservas: ${response.code()}")
                }
            }
        }

    }
}