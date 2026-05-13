package com.raj.slotify.fragments.registry.userRegistry

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.raj.slotify.R
import com.raj.slotify.databinding.FragmentPasswordBinding
import com.raj.slotify.viewModels.frontend.LayoutViewModel
import com.raj.slotify.viewModels.frontend.MainViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import androidx.core.view.isGone

class PasswordFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val layoutViewModel: LayoutViewModel by activityViewModels()
    private val viewModelData: UserDataViewModel by activityViewModels()
    private var isClient: Boolean = false

    private lateinit var binding: FragmentPasswordBinding

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
        viewModel.setNewTitle(R.string.enter_secure_password)
        viewModel.setNewExplication(R.string.verify_password_security)

        layoutViewModel.setExplicationVisibility(View.VISIBLE)

        binding = FragmentPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val passwordEditText: TextInputEditText = binding.inputPassword
        val confirmPasswordText: TextInputEditText = binding.confirmPassword

        // SET THE PASSWORDS OF THE VIEWMODEL IF EXISTS
        val passwordViewModel: String? = viewModelData.password.value

        if (passwordViewModel != null) {
            passwordEditText.setText(passwordViewModel)
            confirmPasswordText.setText(passwordViewModel)
        }

        val securityText = binding.securityLevel
        val labelBase = getString(R.string.security_level)

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES

        securityText.text = "$labelBase: ${getString(R.string.low_level)}"
        securityText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (isDarkMode) android.R.color.holo_red_light else android.R.color.holo_red_dark
            )
        )
        var passwordLevel = "low"

        passwordEditText.doOnTextChanged { text, _, _, _ ->
            if (securityText.isGone)
                securityText.visibility = View.VISIBLE

            val password = text.toString()
            val securityText = binding.securityLevel
            val labelBase = getString(R.string.security_level)

            // Define criteria
            val hasUppercase = password.any { it.isUpperCase() }
            val hasLowercase = password.any { it.isLowerCase() }
            val hasDigit = password.any { it.isDigit() }
            val hasSpecialChar = password.any { !it.isLetterOrDigit() }
            val length = password.length

            // Evaluate the level
            when {
                // HIGH LEVEL: with numbers, uppercases, lowercases and special characters
                length >= 8 && hasUppercase && hasLowercase && hasDigit && hasSpecialChar -> {
                    securityText.text = "$labelBase: ${getString(R.string.high_level)}"
                    securityText.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            android.R.color.holo_green_dark
                        )
                    )
                    passwordLevel = "high"
                }

                // MEDIUM LEVEL: 6+ characters and use at least two types (ej: letters and numbers)
                length >= 6 && ((hasDigit && (hasUppercase || hasLowercase)) || hasSpecialChar) -> {
                    securityText.text = "$labelBase: ${getString(R.string.medium_level)}"
                    securityText.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            android.R.color.holo_orange_dark
                        )
                    )
                    passwordLevel = "medium"
                }

                // LOW LEVEL: with 4 or less characters
                else -> {
                    securityText.text = "$labelBase: ${getString(R.string.low_level)}"
                    securityText.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            android.R.color.holo_red_dark
                        )
                    )
                    passwordLevel = "low"
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            layoutViewModel.nextButtonClicked.collect {
                if (passwordLevel == "low") {
                    // PASSWORD IS NOT STRONG
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.security_error_title))
                        .setMessage(getString(R.string.low_password_error))
                        .setPositiveButton(getString(R.string.dialog_ok), null)
                        .show()

                } else if (passwordEditText.text.toString() != confirmPasswordText.text.toString()) {
                    // ALERT: PASSWORDS DO NOT MATCH
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.validation_error_title))
                        .setMessage(getString(R.string.passwords_do_not_match))
                        .setPositiveButton(getString(R.string.dialog_retry), null)
                        .show()

                } else {
                    // EXIT: PASSWORD IS STRONG AND MATCHES
                    viewModelData.setPassword(passwordEditText.text.toString())

                    if (isClient) {
                        view.findNavController()
                            .navigate(R.id.action_passwordFragment_to_endRegistryFragment)
                    } else {
                        view.findNavController()
                            .navigate(R.id.action_passwordFragment_to_selectPhysicalDirectionFragment)
                    }
                }
            }
        }
    }
}