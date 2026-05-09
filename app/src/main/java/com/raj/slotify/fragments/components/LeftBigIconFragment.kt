package com.raj.slotify.fragments.components

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.raj.slotify.databinding.FragmentLeftBigIconBinding
import com.raj.slotify.viewModels.MainViewModel

class LeftBigIconFragment : Fragment() {
    private lateinit var binding: FragmentLeftBigIconBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLeftBigIconBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val image: ImageView = binding.bigImage
        val titleText: TextView = binding.titleText

        viewModel.newIcon.observe(viewLifecycleOwner) { icon ->
            image.setImageResource(icon)
        }
        viewModel.newTitle.observe(viewLifecycleOwner) { title ->
            titleText.text = getString(title)
        }

    }

}