package com.raj.slotify.fragments.registry.userRegistry

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.raj.slotify.R
import com.raj.slotify.databinding.FragmentSelectUserTypeBinding
import com.raj.slotify.viewModels.frontend.LayoutViewModel
import com.raj.slotify.viewModels.frontend.MainViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel

class SelectUserTypeFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val layoutViewModel: LayoutViewModel by activityViewModels()
    private val userDataViewModel: UserDataViewModel by activityViewModels()
    private lateinit var binding: FragmentSelectUserTypeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectUserTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setNewTitle(R.string.selectUserType)

        layoutViewModel.setBackButtonVisibility(View.VISIBLE)
        layoutViewModel.setNextButtonVisibility(View.GONE)
        layoutViewModel.setCancelButtonVisibility(View.GONE)
        layoutViewModel.setConfirmButtonVisibility(View.GONE)

        val cardViewClient: CardView = binding.cardViewClient
        val cardViewEnterprise: CardView = binding.cardViewEnterprise

        viewModel.setOldTitle(viewModel.newTitle.value?: R.string.welcome)

        cardViewClient.setOnClickListener {
            userDataViewModel.setUserType("USER")
            view.findNavController().navigate(R.id.action_selectUserTypeFragment_to_clientRegistryPhoneEmailFragment)
        }

        cardViewEnterprise.setOnClickListener {
            userDataViewModel.setUserType("COMPANY")
            view.findNavController().navigate(R.id.action_selectUserTypeFragment_to_clientRegistryPhoneEmailFragment)
        }

    }
}