package com.example.slotify.fragments.registry.userRegistry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.slotify.R
import com.example.slotify.databinding.FragmentRegistryBinding
import com.example.slotify.viewModels.EnterpriseRegistryViewModel
import com.example.slotify.viewModels.LayoutViewModel
import com.example.slotify.viewModels.MainViewModel

class RegistryFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val enterpriseViewModel: EnterpriseRegistryViewModel by activityViewModels()
    private val layoutViewModel: LayoutViewModel by activityViewModels()

    private lateinit var binding: FragmentRegistryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layoutViewModel.setSuperiorFragmentVisibility(View.VISIBLE)
        layoutViewModel.setInferiorFragmentVisibility(View.VISIBLE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize view models
        viewModel.let { }
        enterpriseViewModel.let { }
        layoutViewModel.let { }

        viewModel.setNewTitle(R.string.selectUserType)
        layoutViewModel.setNextButtonVisibility(View.GONE)

        val superiorFragment: FragmentContainerView = binding.fragmentSuperior
        val inferiorFragment: FragmentContainerView = binding.framentInferior
        val backButton: Button = binding.goBackButton
        val nextButton: Button = binding.advanceButton

        layoutViewModel.superiorFragmentVisibility.observe(viewLifecycleOwner) { visible ->
            superiorFragment.visibility = visible
        }

        layoutViewModel.inferiorFragmentVisibility.observe(viewLifecycleOwner) { visible ->
            inferiorFragment.visibility = visible
        }

        layoutViewModel.backButtonVisibility.observe(viewLifecycleOwner) { visible ->
            backButton.visibility = visible
        }

        layoutViewModel.nextButtonVisibility.observe(viewLifecycleOwner) { visible ->
            nextButton.visibility = visible
        }

        // Search internal nav host to navigate back correctly
        val navHostFragment = childFragmentManager.findFragmentById(R.id.framentInferior) as NavHostFragment
        val innerNavController = navHostFragment.navController

        binding.goBackButton.setOnClickListener {

            // Down NavHost can go back?
            if (innerNavController.previousBackStackEntry != null) {
                // YES: go from ClientRegistryPhoneEmail a SelectUserType, for example
                innerNavController.popBackStack()
            } else {
                // NO: We are in registry start, go back to FirstFragment
                // Use normal findNavController()

                findNavController().popBackStack()
            }
        }

        binding.advanceButton.setOnClickListener {
            layoutViewModel.onNextClicked()
        }

        // Modify back button behaviour (System Back Button)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (innerNavController.previousBackStackEntry != null) {
                        innerNavController.popBackStack()
                    } else {
                        findNavController().popBackStack()
                    }
                }
            })
    }
}