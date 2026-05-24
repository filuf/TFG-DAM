package com.raj.slotify.fragments.reserves.client.createReserve

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.raj.slotify.R
import com.raj.slotify.databinding.FragmentFirstReserveCreationBinding
import com.raj.slotify.dtos.reserves.UserReserveSummary
import com.raj.slotify.models.TextModel
import com.raj.slotify.viewModels.frontend.ClientReservesViewModel
import com.raj.slotify.viewModels.frontend.LayoutViewModel
import com.raj.slotify.viewModels.frontend.MainViewModel

class FirstReserveCreationFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val layoutViewModel: LayoutViewModel by activityViewModels()
    private val clientReservesViewModel: ClientReservesViewModel by activityViewModels()

    private lateinit var binding: FragmentFirstReserveCreationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layoutViewModel.setCancelButtonVisibility(View.VISIBLE)
        layoutViewModel.setBackButtonVisibility(View.GONE)
        layoutViewModel.setConfirmButtonVisibility(View.GONE)

        layoutViewModel.setImageVisibility(View.GONE)
        layoutViewModel.setDescriptionVisibility(View.VISIBLE)
        layoutViewModel.setExplicationVisibility(View.VISIBLE)

        mainViewModel.setTitle(TextModel(R.string.reserve_appointment))
        mainViewModel.setSubtitle(TextModel(R.string.service_to_reserve_question))
        mainViewModel.setExplication(TextModel(R.string.select_cattegory_message))

        binding = FragmentFirstReserveCreationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val customCatText = binding.customCatText
        val searchButton = binding.customSearchLayout

        val restaurantCat = binding.cardHostelery
        val beautyCat = binding.cardStetic
        val travelsCat = binding.cardTravels

        fun goToNextPage(category: String) {
            clientReservesViewModel.setCategoryToReserve(category)
            view.findNavController().navigate(R.id.action_firstReserveCreationFragment_to_listCompaniesToReserveFragment)
        }

        restaurantCat.setOnClickListener {
            goToNextPage("restaurants")
        }
        beautyCat.setOnClickListener {
            goToNextPage("beauty")
        }
        travelsCat.setOnClickListener {
            goToNextPage("travels")
        }
        searchButton.setOnClickListener {
            goToNextPage(customCatText.text.toString())
        }

    }

}