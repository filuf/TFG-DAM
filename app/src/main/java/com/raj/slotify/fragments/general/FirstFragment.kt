package com.raj.slotify.fragments.general

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.raj.slotify.R
import com.raj.slotify.activities.LogInActivity
import com.raj.slotify.activities.RegistryActivity
import com.raj.slotify.databinding.FragmentFirstBinding
import com.raj.slotify.models.TextModel
import com.raj.slotify.viewModels.frontend.LayoutViewModel
import com.raj.slotify.viewModels.frontend.MainViewModel

class FirstFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val layoutViewModel: LayoutViewModel by activityViewModels()
    private lateinit var binding: FragmentFirstBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel.setIcon(R.drawable._logoslotify_retocado)
        viewModel.setTitle(TextModel(R.string.welcome))
        viewModel.setExplication(TextModel(R.string.account_question))

        layoutViewModel.setSecondTextVisibility(View.GONE)
        layoutViewModel.setExplicationVisibility(View.GONE)
        layoutViewModel.setDescriptionVisibility(View.GONE)
        layoutViewModel.setImageVisibility(View.VISIBLE)

        binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val textExplication: TextView = binding.explicationText
        val changeLanguageLayout: ConstraintLayout = binding.changeLanguageLayout
        val logOnButton: Button = binding.buttonLogOn
        val logInButton: Button = binding.buttonLogIn

        viewModel.explication.observe(viewLifecycleOwner) { explication ->
            textExplication.text = if (explication.stringId != null) getString(explication.stringId) else explication.customText?: ""
        }

        changeLanguageLayout.setOnClickListener {
            view.findNavController().navigate(R.id.action_firstFragment_to_languageFragment)
        }

        logOnButton.setOnClickListener {
            val intent = Intent(requireContext(), RegistryActivity::class.java)
            startActivity(intent)
        }

        logInButton.setOnClickListener {
            val intent = Intent(requireContext(), LogInActivity::class.java)
            startActivity(intent)
        }

    }

}