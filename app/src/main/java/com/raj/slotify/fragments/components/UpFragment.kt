package com.raj.slotify.fragments.components

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.activityViewModels
import com.raj.slotify.R
import com.raj.slotify.databinding.FragmentUpBinding
import com.raj.slotify.viewModels.frontend.LayoutViewModel
import com.raj.slotify.viewModels.frontend.MainViewModel

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
        val cardIcon: CardView = binding.cardIcon
        val titleText: TextView = binding.titleText
        val descText: TextView = binding.textDesc
        val explicationText: TextView = binding.textExplication

        viewModel.icon.observe(viewLifecycleOwner) { icon ->
            image.setImageResource(icon)
        }
        viewModel.title.observe(viewLifecycleOwner) { title ->
            titleText.text = if (title.stringId != null) getString(title.stringId) else title.customText?: ""
        }
        viewModel.subtitle.observe(viewLifecycleOwner) { desc ->
            descText.text = if (desc.stringId != null) getString(desc.stringId) else desc.customText?: ""
        }
        viewModel.explication.observe(viewLifecycleOwner) { explication ->
            explicationText.text = if (explication.stringId != null) getString(explication.stringId) else explication.customText?: ""
        }

        layoutViewModel.imageVisibility.observe(viewLifecycleOwner) { visibility ->
            cardIcon.visibility = visibility
        }

        layoutViewModel.descriptionVisibility.observe(viewLifecycleOwner) { visibility ->
            descText.visibility = visibility
        }

        layoutViewModel.explicationVisibility.observe(viewLifecycleOwner) { visibility ->
            explicationText.visibility = visibility
        }

    }

}