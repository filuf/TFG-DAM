package com.raj.slotify.fragments.registry.userRegistry

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import com.raj.slotify.R
import com.raj.slotify.databinding.FragmentEndRegistryBinding
import com.raj.slotify.viewModels.EnterpriseRegistryViewModel
import com.raj.slotify.viewModels.LayoutViewModel
import com.raj.slotify.viewModels.MainViewModel
import com.raj.slotify.viewModels.UserDataViewModel

class EndRegistryFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val viewModelData: UserDataViewModel by activityViewModels()
    private val enterpriseViewModel: EnterpriseRegistryViewModel by activityViewModels()
    private val layoutViewModel: LayoutViewModel by activityViewModels()

    private lateinit var binding: FragmentEndRegistryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        layoutViewModel.setInferiorFragmentVisibility(View.GONE)
        layoutViewModel.setExplicationVisibility(View.VISIBLE)

        viewModel.setNewTitle(R.string.wait_a_moment)
        viewModel.setNewExplication(R.string.wait_a_moment_expication)

        layoutViewModel.setBackButtonVisibility(View.GONE)
        layoutViewModel.setNextButtonVisibility(View.GONE)

        binding = FragmentEndRegistryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // BLOCK SYSTEM BACK BUTTON
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // FUNCTION EMPTY TO BLOCK
            }
        })

        // TODO: CONNECT TO SPRING SERVER AND SAVE DATA
    }

}