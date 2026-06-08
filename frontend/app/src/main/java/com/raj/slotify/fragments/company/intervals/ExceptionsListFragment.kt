package com.raj.slotify.fragments.company.intervals

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.raj.slotify.R
import com.raj.slotify.activities.LogInActivity
import com.raj.slotify.adapters.IntervalsListAdapter
import com.raj.slotify.databinding.FragmentExceptionsListBinding
import com.raj.slotify.viewModels.apiRest.IntervalViewModel
import com.raj.slotify.viewModels.frontend.MainViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel

class ExceptionsListFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val userDataViewModel: UserDataViewModel by activityViewModels()
    private val intervalViewModel: IntervalViewModel by activityViewModels()

    private lateinit var binding: FragmentExceptionsListBinding
    private lateinit var adapter: IntervalsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainViewModel.setTitle(com.raj.slotify.models.TextModel(R.string.exceptions_word))
        binding = FragmentExceptionsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView(view)
        loadIntervals()
        observeIntervals()

        binding.addExceptionButton.setOnClickListener {
            view.findNavController()
                .navigate(R.id.action_exceptionsListFragment_to_addExceptionFragment)
        }

        intervalViewModel.intervalCreated.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                intervalViewModel.clearCreated()
                loadIntervals()
            }
        }

        intervalViewModel.intervalUpdated.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                intervalViewModel.clearUpdated()
                loadIntervals()
            }
        }

        intervalViewModel.intervalDeletedResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                intervalViewModel.clearDeleted()
                loadIntervals()
            }
        }
    }

    private fun setupRecyclerView(view: View) {
        adapter = IntervalsListAdapter(mutableListOf()) { interval ->
            intervalViewModel.selectInterval(interval)
            view.findNavController()
                .navigate(R.id.action_exceptionsListFragment_to_addExceptionFragment)
        }
        binding.exceptionsRecycler.adapter = adapter
        binding.exceptionsRecycler.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadIntervals() {
        val token = userDataViewModel.userToken.value?.accessToken ?: return
        intervalViewModel.getIntervals("Bearer $token")
    }

    private fun observeIntervals() {
        intervalViewModel.intervals.observe(viewLifecycleOwner) { response ->
            if (response == null) return@observe
            when {
                response.code() == 401 -> redirectToLogin()
                response.code() == 204 -> adapter.setItems(emptyList())
                response.isSuccessful -> adapter.setItems(response.body() ?: emptyList())
            }
        }
    }

    private fun redirectToLogin() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.sesion_expired_title))
            .setMessage(getString(R.string.sesion_expired_explication))
            .setPositiveButton(getString(R.string.dialog_ok)) { _, _ ->
                startActivity(Intent(requireActivity(), LogInActivity::class.java))
            }.show()
    }
}
