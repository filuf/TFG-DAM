package com.raj.slotify.fragments.components

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.raj.slotify.databinding.FragmentCenterTextNormalIconBinding
import com.raj.slotify.viewModels.frontend.LayoutViewModel
import com.raj.slotify.viewModels.frontend.MainViewModel

class CenterTextNormalIconFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val layoutViewModel: LayoutViewModel by activityViewModels()
    private lateinit var binding: FragmentCenterTextNormalIconBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCenterTextNormalIconBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleText: TextView = binding.titleText
        val secondText: TextView = binding.secondText
        val image: ImageView = binding.bigImage

        viewModel.icon.observe(viewLifecycleOwner) { icon ->
            image.setImageResource(icon)
        }
        viewModel.title.observe(viewLifecycleOwner) { title ->
            titleText.text = if (title.stringId != null) getString(title.stringId) else title.customText?: ""
        }
        viewModel.secondTitle.observe(viewLifecycleOwner) { secondTitle ->
            secondText.text = secondTitle
        }

        layoutViewModel.secondTextVisibility.observe(viewLifecycleOwner) { visible ->
            secondText.visibility = visible
        }
    }
}