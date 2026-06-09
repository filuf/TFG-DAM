package com.raj.slotify.fragments.registry.userRegistry

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.raj.slotify.R
import com.raj.slotify.activities.LogInActivity
import com.raj.slotify.databinding.FragmentEndRegistryBinding
import com.raj.slotify.dtos.registry.client.RegisterUserRequest
import com.raj.slotify.dtos.registry.company.RegisterCompanyRequest
import com.raj.slotify.models.TextModel
import com.raj.slotify.viewModels.apiRest.RegisterUserViewModel
import com.raj.slotify.viewModels.frontend.EnterpriseDataViewModel
import com.raj.slotify.viewModels.frontend.LayoutViewModel
import com.raj.slotify.viewModels.frontend.MainViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel

class EndRegistryFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val userDataViewModel: UserDataViewModel by activityViewModels()
    private val enterpriseViewModel: EnterpriseDataViewModel by activityViewModels()
    private val layoutViewModel: LayoutViewModel by activityViewModels()

    private val registerUserViewModel: RegisterUserViewModel by activityViewModels()

    private lateinit var binding: FragmentEndRegistryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layoutViewModel.setInferiorFragmentVisibility(View.GONE)
        layoutViewModel.setExplicationVisibility(View.VISIBLE)

        viewModel.setTitle(TextModel(R.string.wait_a_moment))
        viewModel.setExplication(TextModel(R.string.wait_a_moment_expication))

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

        val userType: String = userDataViewModel.userType.value?: "USER"

        if (userType == "USER") {
            Log.i(userType, "Registrando cliente")
            registerClient()
        } else if (userType == "COMPANY") {
            Log.i(userType, "Registrando empresa")
            registerCompany()
        }

        binding.logInAccountCreatedButton.setOnClickListener {
            val intent = Intent(requireContext(), LogInActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

    }

    private fun performClientRegistry() {
        val registerClientRequest = RegisterUserRequest(
            userDataViewModel.name.value,
            userDataViewModel.password.value,
            userDataViewModel.email.value
        )
        registerUserViewModel.registerClient(registerClientRequest)
        Log.i("CLIENT", "Petición de registro hecha")
    }

    private fun performCompanyRegistry() {
        userDataViewModel.name.observe(viewLifecycleOwner) { name ->
            enterpriseViewModel.concurrentServices.observe(viewLifecycleOwner) { concurrentServices ->
                var physicalAddress: String? = null
                if(enterpriseViewModel.ubicationPlaceSuggestion.value != null)
                    physicalAddress = enterpriseViewModel.ubicationPlaceSuggestion.value!!.displayName

                val registerCompanyRequest = RegisterCompanyRequest(
                    name,
                    userDataViewModel.password.value,
                    userDataViewModel.email.value,
                    concurrentServices,
                    physicalAddress
                )
                registerUserViewModel.registerCompany(registerCompanyRequest)
                Log.i("COMPANY", "Petición de registro hecha")

                enterpriseViewModel.concurrentServices.removeObservers(viewLifecycleOwner)
                userDataViewModel.name.removeObservers(viewLifecycleOwner)
            }
        }
    }

    private fun exitWithoutRegistry() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.cancel_registry_confirmation))
            .setMessage(R.string.cancel_registry_explication)
            .setPositiveButton(getString(R.string.dialog_ok)) { _, _ ->
                layoutViewModel.setInferiorFragmentVisibility(View.VISIBLE)
                view?.findNavController()?.popBackStack()
                layoutViewModel.setBackButtonVisibility(View.VISIBLE)
                layoutViewModel.setNextButtonVisibility(View.VISIBLE)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun successfulRegistryConfirmation() {
        viewModel.setTitle(TextModel(R.string.succesful_account_creation))
        layoutViewModel.setExplicationVisibility(View.GONE)
        layoutViewModel.setInferiorFragmentVisibility(View.VISIBLE)
    }

    private fun registerClient() {
        registerUserViewModel.clientRegistered.observe(viewLifecycleOwner) { clientRegistered ->
            Log.i("Client registered", clientRegistered?.toString()?: "null")
            if (clientRegistered != null) {
                if (clientRegistered.isSuccessful) {
                    Log.w("Registered", "Cliente registrado correctamente: ${clientRegistered.body()}")
                    successfulRegistryConfirmation()

                } else if (clientRegistered.code() == 409) {
                    Log.e("[${clientRegistered.code()}] Empresa registrada", clientRegistered.body().toString())
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.account_no_created))
                        .setMessage(getString(R.string.account_already_exists))
                        .setPositiveButton(getString(R.string.dialog_ok)) { _, _ ->
                            view?.findNavController()?.navigate(R.id.action_endRegistryFragment_to_registryPhoneEmailFragment2)
                        }
                        .show()

                } else {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.account_no_created))
                        .setMessage(clientRegistered.message())
                        .setPositiveButton(getString(R.string.dialog_retry)) { _, _ ->
                            performClientRegistry()
                        }
                        .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                            exitWithoutRegistry()
                        }
                        .show()
                }
            }
        }
        performClientRegistry()
    }

    private fun registerCompany() {
        registerUserViewModel.companyRegistered.observe(viewLifecycleOwner) { companyRegistered ->
            Log.i("Client registered", companyRegistered?.toString()?: "null")
            if (companyRegistered != null) {
                if (companyRegistered.isSuccessful) {
                    Log.w("Registered", "Empresa registrada correctamente")
                    successfulRegistryConfirmation()

                } else if (companyRegistered.code() == 409) {
                    Log.e("[${companyRegistered.code()}] Empresa registrada", companyRegistered.body().toString())
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.account_no_created))
                        .setMessage(getString(R.string.account_already_exists))
                        .setPositiveButton(getString(R.string.dialog_ok)) { _, _ ->
                            view?.findNavController()?.navigate(R.id.action_endRegistryFragment_to_registryPhoneEmailFragment2)
                            layoutViewModel.setBackButtonVisibility(View.VISIBLE)
                            layoutViewModel.setNextButtonVisibility(View.VISIBLE)
                        }
                        .show()
                } else {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.account_no_created))
                        .setMessage(companyRegistered.message())
                        .setPositiveButton(getString(R.string.dialog_retry)) { _, _ ->
                            performCompanyRegistry()
                        }
                        .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                            exitWithoutRegistry()
                        }
                        .show()
                }
            }
        }
        performCompanyRegistry()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        layoutViewModel.setBackButtonVisibility(View.VISIBLE)
        layoutViewModel.setNextButtonVisibility(View.VISIBLE)
    }

}