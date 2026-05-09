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
import com.raj.slotify.viewModels.MainViewModel

class CenterTextNormalIconFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
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

        val text: TextView = binding.titleText
        val image: ImageView = binding.bigImage

        viewModel.newIcon.observe(viewLifecycleOwner) { icon ->
            image.setImageResource(icon)
        }
        viewModel.newTitle.observe(viewLifecycleOwner) { title ->
            text.text = getString(title)
        }

    }
}