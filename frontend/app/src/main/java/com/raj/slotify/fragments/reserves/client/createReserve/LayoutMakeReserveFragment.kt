package com.raj.slotify.fragments.reserves.client.createReserve

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.raj.slotify.R
import com.raj.slotify.databinding.FragmentLayoutMakeReserveBinding
import com.raj.slotify.viewModels.frontend.LayoutViewModel
import com.raj.slotify.viewModels.frontend.MainViewModel

class LayoutMakeReserveFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val layoutViewModel: LayoutViewModel by activityViewModels()

    private lateinit var binding: FragmentLayoutMakeReserveBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLayoutMakeReserveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Search internal nav host to navigate back correctly
        val navHostFragment = childFragmentManager.findFragmentById(R.id.fragmentDownCreateReserve) as NavHostFragment
        val innerNavController = navHostFragment.navController

        val cancelButton = binding.cancelNewReserveButton
        val goBackButton = binding.goBackNewReserveButton
        val confirmButton = binding.confirmNewReserveButton

        cancelButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.reserve_cancelation_title))
                .setMessage(getString(R.string.reserve_cancelation_confirmation))
                .setNegativeButton(getString(R.string.negation_word), null)
                .setPositiveButton(getString(R.string.yes_word)) { dialog, which ->
                    requireActivity().finish()
                }.show()
        }

        goBackButton.setOnClickListener {
            // Down NavHost can go back?
            if (innerNavController.previousBackStackEntry != null) {
                // YES: go from ClientRegistryPhoneEmail a SelectUserType, for example
                innerNavController.popBackStack()
            } else {
                // NO: We are in registry start, go back to FirstFragment
                // Use normal findNavController()
                requireActivity().finish()
            }
        }

        confirmButton.setOnClickListener {
            Toast.makeText(requireContext(), "Has confirmado", Toast.LENGTH_SHORT).show()
        }

        // Set buttons visibility observers
        layoutViewModel.cancelButtonVisibility.observe(viewLifecycleOwner) { visibility ->
            cancelButton.visibility = visibility
        }
        layoutViewModel.backButtonVisibility.observe(viewLifecycleOwner) { visibility ->
            goBackButton.visibility = visibility
        }
        layoutViewModel.confirmButtonVisibility.observe(viewLifecycleOwner) { visibility ->
            confirmButton.visibility = visibility
        }

        // Modify back button behaviour (System Back Button)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (innerNavController.previousBackStackEntry != null) {
                        innerNavController.popBackStack()
                    } else {
                        requireActivity().finish()
                    }
                }
            }
        )


    }

}