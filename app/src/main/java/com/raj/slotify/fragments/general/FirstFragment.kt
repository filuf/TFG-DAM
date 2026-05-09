package com.raj.slotify.fragments.general

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.raj.slotify.R
import com.raj.slotify.databinding.FragmentFirstBinding
import com.raj.slotify.viewModels.LayoutViewModel
import com.raj.slotify.viewModels.MainViewModel

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

        viewModel.setNewIcon(R.drawable._logoslotify_retocado)
        viewModel.setNewTitle(R.string.welcome)
        viewModel.setNewExplication(R.string.account_question)

        layoutViewModel.setExplicationVisibility(View.GONE)
        layoutViewModel.setDescriptionVisibility(View.GONE)
        layoutViewModel.setImageVisibility(View.VISIBLE)

        binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textExplication: TextView = binding.explicationText
        val changeLanguageLayout: ConstraintLayout = binding.changeLanguageLayout
        val logOnButton: Button = binding.buttonLogOn
        val logInButton: Button = binding.buttonLogIn

        viewModel.newExplication.observe(viewLifecycleOwner) { explication ->
            textExplication.text = getString(explication)
        }

        changeLanguageLayout.setOnClickListener {
            view.findNavController().navigate(R.id.action_firstFragment_to_languageFragment)

            viewModel.setOldTitle(viewModel.newTitle.value ?: R.string.welcome)
        }

        logOnButton.setOnClickListener {
            view.findNavController().navigate(R.id.action_firstFragment_to_registryFragment2)

            viewModel.setOldTitle(viewModel.newTitle.value ?: R.string.welcome)
        }

    }

}