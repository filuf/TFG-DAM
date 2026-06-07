package com.raj.slotify.fragments.general

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.raj.slotify.R
import com.raj.slotify.activities.EditUserDataActivity
import com.raj.slotify.databinding.FragmentSettingsBinding
import com.raj.slotify.viewModels.frontend.UserDataViewModel

class SettingsFragment : Fragment() {
    private val userDataViewModel: UserDataViewModel by activityViewModels()

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editAccountCard.setOnClickListener {
            userDataViewModel.userType.let { userType ->
                val intent = Intent(requireActivity(), EditUserDataActivity::class.java)

                Log.i("SettingsFragment", "User type: ${userType.value}, uuid: ${userDataViewModel.uuid.value}")

                intent.putExtra("ID", userDataViewModel.uuid.value.toString())
                intent.putExtra("USER_TYPE", userType.value)
                startActivity(intent)
            }
        }

        binding.changueLanguageCard.setOnClickListener {
            view.findNavController().navigate(R.id.action_settingsFragment_to_languageFragment)
        }

    }

}