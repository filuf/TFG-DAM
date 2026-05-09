package com.raj.slotify.fragments.components

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.raj.slotify.databinding.FragmentUpBinding
import com.raj.slotify.viewModels.LayoutViewModel
import com.raj.slotify.viewModels.MainViewModel

class UpFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val layoutViewModel: LayoutViewModel by activityViewModels()

    private lateinit var binding: FragmentUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val image: ImageView = binding.bigImage
        val titleText: TextView = binding.titleText
        val descText: TextView = binding.textDesc
        val explicationText: TextView = binding.textExplication

        viewModel.newIcon.observe(viewLifecycleOwner) { icon ->
            image.setImageResource(icon)
        }
        viewModel.newTitle.observe(viewLifecycleOwner) { title ->
            titleText.text = getString(title)
        }
        viewModel.newExplication.observe(viewLifecycleOwner) { explication ->
            explicationText.text = getString(explication)
        }

        layoutViewModel.imageVisibility.observe(viewLifecycleOwner) { visibility ->
            image.visibility = visibility
        }

        layoutViewModel.descriptionVisibility.observe(viewLifecycleOwner) { visibility ->
            descText.visibility = visibility
        }

        layoutViewModel.explicationVisibility.observe(viewLifecycleOwner) { visibility ->
            explicationText.visibility = visibility
        }

    }

}