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
import com.raj.slotify.databinding.FragmentRegistryPhoneEmailBinding
import com.raj.slotify.viewModels.LayoutViewModel
import com.raj.slotify.viewModels.MainViewModel
import com.raj.slotify.viewModels.UserDataViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class RegistryPhoneEmailFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val viewModelData: UserDataViewModel by activityViewModels()
    private val layoutViewModel: LayoutViewModel by activityViewModels()
    private var isClient: Boolean = false
    private lateinit var binding: FragmentRegistryPhoneEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUserType: String = viewModelData.userType.value ?: "client"

        if (currentUserType == "client") {
            isClient = true
        } else if (currentUserType == "company") {
            isClient = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistryPhoneEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutViewModel.setNextButtonVisibility(View.VISIBLE)

        if (isClient) {
            viewModel.setNewTitle(R.string.enter_user_data)
            binding.inputLayoutUserName.hint = getString(R.string.enter_user_name)
            binding.contactDataText.text = getString(R.string.user_data)
        } else {
            viewModel.setNewTitle(R.string.enter_organitation_data)
            binding.inputLayoutUserName.hint = getString(R.string.company_name)
            binding.contactDataText.text = getString(R.string.contact_data)
        }

        val ccp = binding.ccp
        val phoneInput = binding.phoneInput
        ccp.registerCarrierNumberEditText(phoneInput)

        viewLifecycleOwner.lifecycleScope.launch {
            layoutViewModel.nextButtonClicked.collect {

                val userName: String = binding.userNameText.text.toString()
                val phone: String = ccp.fullNumberWithPlus
                val isValid = ccp.isValidFullNumber
                val email: String = binding.emailInput.text.toString()

                if (email.isEmpty() ||
                    phone.isEmpty() ||
                    userName.isEmpty()) {

                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.email_phone_empty_title))
                        .setMessage(getString(R.string.email_phone_empty_camps))
                        .setPositiveButton(getString(R.string.dialog_retry), null)
                        .show()
                }
                else if (!isValid) {

                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.phone_invalid_title))
                        .setMessage(getString(R.string.phone_invalid_message))
                        .setPositiveButton(getString(R.string.dialog_retry), null)
                        .show()

                } else {
                    // TODO: CONECTAR A BASE DE DATOS Y VERIFICAR QUE EXISTA UN USUARIO CON ESE EMAIL O TELÉFONO

                    var phoneNumberExists = false
                    var emailExists = false

                    if (phoneNumberExists || emailExists) {
                        // TODO: REDIRIGIR AL USUARIO A LA PANTALLA DE INICIO
                    }

                    viewModelData.setPhone(phone)
                    viewModelData.setEmail(email)
                    viewModelData.setName(userName)

                    view.findNavController()
                        .navigate(R.id.action_clientRegistryPhoneEmailFragment2_to_passwordFragment)
                }
            }
        }
    }

}