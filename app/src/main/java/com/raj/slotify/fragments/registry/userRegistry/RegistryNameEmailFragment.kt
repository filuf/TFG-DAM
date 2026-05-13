package com.raj.slotify.fragments.registry.userRegistry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.raj.slotify.R
import com.raj.slotify.viewModels.frontend.LayoutViewModel
import com.raj.slotify.viewModels.frontend.MainViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.raj.slotify.databinding.FragmentRegistryNameEmailBinding
import com.raj.slotify.viewModels.apiRest.RegisterUserViewModel
import kotlinx.coroutines.launch

class RegistryNameEmailFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val viewModelData: UserDataViewModel by activityViewModels()
    private val layoutViewModel: LayoutViewModel by activityViewModels()
    private val registerUserViewModel: RegisterUserViewModel by activityViewModels()
    private var isClient: Boolean = false
    private lateinit var binding: FragmentRegistryNameEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUserType: String = viewModelData.userType.value ?: "USER"

        if (currentUserType == "USER") {
            isClient = true
        } else if (currentUserType == "COMPANY") {
            isClient = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistryNameEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutViewModel.setNextButtonVisibility(View.VISIBLE)

        if (isClient) {
            viewModel.setNewTitle(R.string.enter_user_data)
            binding.inputLayoutUserName.hint = getString(R.string.enter_user_name)
        } else {
            viewModel.setNewTitle(R.string.enter_organitation_data)
            binding.inputLayoutUserName.hint = getString(R.string.company_name)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            layoutViewModel.nextButtonClicked.collect {

                val userName: String = binding.userNameText.text.toString()
                val email: String = binding.emailInput.text.toString()

                if (email.isEmpty() ||
                    userName.isEmpty()) {

                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.email_phone_empty_title))
                        .setMessage(getString(R.string.email_phone_empty_camps))
                        .setPositiveButton(getString(R.string.dialog_retry), null)
                        .show()

                } else {
                    // TODO: CONECTAR A BASE DE DATOS Y VERIFICAR QUE EXISTA UN USUARIO CON ESE EMAIL O TELÉFONO

                    var phoneNumberExists = false
                    var emailExists = false

                    if (phoneNumberExists || emailExists) {
                        // TODO: REDIRIGIR AL USUARIO A LA PANTALLA DE INICIO
                    }

                    viewModelData.setEmail(email)
                    viewModelData.setName(userName)

                    view.findNavController()
                        .navigate(R.id.action_clientRegistryPhoneEmailFragment2_to_passwordFragment)
                }
            }
        }
    }

}