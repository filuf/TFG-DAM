package com.raj.slotify.fragments.reserves.client.createReserve

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.raj.slotify.R
import com.raj.slotify.activities.MainActivity
import com.raj.slotify.databinding.FragmentReserveLastScreenBinding
import com.raj.slotify.models.TextModel
import com.raj.slotify.tools.MessageAlerts
import com.raj.slotify.tools.Verifier
import com.raj.slotify.viewModels.apiRest.ReservesViewModel
import com.raj.slotify.viewModels.frontend.LayoutViewModel
import com.raj.slotify.viewModels.frontend.MainViewModel

class ReserveLastScreenFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val layoutViewModel: LayoutViewModel by activityViewModels()
    private val reservesViewModel: ReservesViewModel by activityViewModels()

    private lateinit var binding: FragmentReserveLastScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainViewModel.setTitle(TextModel(R.string.wait_a_moment))
        mainViewModel.setSubtitle(TextModel(R.string.wait_while_reserving))

        layoutViewModel.setCancelButtonVisibility(View.GONE)
        layoutViewModel.setConfirmButtonVisibility(View.GONE)
        layoutViewModel.setBackButtonVisibility(View.GONE)

        binding = FragmentReserveLastScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressBarEndReserve.visibility = View.VISIBLE
        binding.endReserveLayout.visibility = View.GONE

        observeReserveCreated()
        setButtonBehaviour()

    }

    fun setButtonBehaviour() {
        binding.backToHomeButton.setOnClickListener {
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
        }
    }

    fun observeReserveCreated() {
        reservesViewModel.reserveCrated.observe(viewLifecycleOwner) { reserve ->
            binding.progressBarEndReserve.visibility = View.VISIBLE
            binding.endReserveLayout.visibility = View.GONE

            if (reserve == null || !Verifier.verifySuccessfulResponse(
                reserve,
                requireContext(),
                positiveAction = {view?.findNavController()?.popBackStack()},
                positiveActionText = getString(R.string.dialog_retry),
                negativeAction = { MessageAlerts.showCancelAppointmentButton(
                    requireContext(),
                    onCancel = {view?.findNavController()?.popBackStack()},
                    onConfirm = {requireActivity().finish()}
                )},
                negativeActionText = getString(R.string.cancel_reserve)
            )) {
                return@observe
            }

            val responseBody = reserve.body()!!

            mainViewModel.setTitle(TextModel(R.string.succesful_reserve))

            binding.progressBarEndReserve.visibility = View.GONE
            binding.endReserveLayout.visibility = View.VISIBLE

            binding.serviceSummaryNameText.text = responseBody.serviceName
            binding.companyNameSummaryText.text = responseBody.companyName
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        layoutViewModel.setCancelButtonVisibility(View.GONE)
        layoutViewModel.setConfirmButtonVisibility(View.VISIBLE)
    }

}