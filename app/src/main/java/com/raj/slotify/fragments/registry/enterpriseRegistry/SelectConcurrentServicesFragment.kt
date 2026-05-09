package com.raj.slotify.fragments.registry.enterpriseRegistry

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.raj.slotify.R
import com.raj.slotify.databinding.FragmentSelectConcurrentServicesBinding
import com.raj.slotify.viewModels.EnterpriseRegistryViewModel
import com.raj.slotify.viewModels.LayoutViewModel
import com.raj.slotify.viewModels.MainViewModel
import kotlinx.coroutines.launch
import kotlin.getValue

class SelectConcurrentServicesFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val layoutViewModel: LayoutViewModel by activityViewModels()
    private val enterpriseViewModel: EnterpriseRegistryViewModel by activityViewModels()

    private lateinit var binding: FragmentSelectConcurrentServicesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        layoutViewModel.setExplicationVisibility(View.VISIBLE)

        viewModel.setNewTitle(R.string.enter_simultaneous_services)
        viewModel.setNewExplication(R.string.simultaneous_numer_explication)

        binding = FragmentSelectConcurrentServicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // FIND THE NUMBER PICKER VIEW BY ITS ID
        val numberPicker = binding.numberPicker

        // SET THE MINIMUM AND MAXIMUM VALUES FOR THE PICKER
        numberPicker.minValue = 1
        numberPicker.maxValue = 100

        // SET THE INITIAL VALUE TO BE DISPLAYED
        numberPicker.value = 1

        // ENABLE OR DISABLE WRAPPING AROUND THE VALUES
        numberPicker.wrapSelectorWheel = true

        // CAPTURE THE VALUE CHANGE EVENT TO RESPOND TO USER INPUT
        numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            // LOG OR USE THE NEW SELECTED NUMBER
            println("SELECTED NUMBER IS: $newVal")
        }

        viewLifecycleOwner.lifecycleScope.launch {
            layoutViewModel.nextButtonClicked.collect {
                view.findNavController().navigate(R.id.action_selectConcurrentServicesFragment_to_endRegistryFragment)
                enterpriseViewModel.setConcurrentServices(numberPicker.value)
            }
        }
    }
}