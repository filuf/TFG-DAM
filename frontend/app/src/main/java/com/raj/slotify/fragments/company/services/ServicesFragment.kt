package com.raj.slotify.fragments.company.services

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.raj.slotify.R
import com.raj.slotify.activities.LogInActivity
import com.raj.slotify.activities.company.AddEditServiceActivity
import com.raj.slotify.adapters.ServiceListAdapter
import com.raj.slotify.databinding.FragmentServicesBinding
import com.raj.slotify.dtos.company.GetServicesResponse
import com.raj.slotify.models.TextModel
import com.raj.slotify.viewModels.apiRest.CompanyViewModel
import com.raj.slotify.viewModels.frontend.MainViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel

class ServicesFragment : Fragment() {

    private val userDataViewModel: UserDataViewModel by activityViewModels()
    private val companyViewModel: CompanyViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var binding: FragmentServicesBinding
    private lateinit var adapter: ServiceListAdapter

    companion object {
        const val EXTRA_COMPANY_ID = "EXTRA_COMPANY_ID"
        const val EXTRA_AUTH_TOKEN = "EXTRA_AUTH_TOKEN"
        const val EXTRA_SERVICE_ID = "EXTRA_SERVICE_ID"
        const val EXTRA_SERVICE_NAME = "EXTRA_SERVICE_NAME"
        const val REQUEST_ADD_SERVICE = 1001
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainViewModel.setTitle(TextModel(R.string.my_services))
        binding = FragmentServicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()
        setupFab()
        loadServices()
        observeServices()
    }

    private fun setupRecycler() {
        adapter = ServiceListAdapter(mutableListOf()) { service: GetServicesResponse ->
            openAddEditService(service)
        }
        binding.recyclerServices.adapter = adapter
        binding.recyclerServices.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupFab() {
        binding.addServiceButton.setOnClickListener {
            openAddEditService(null)
        }
    }

    private fun loadServices() {
        val companyId = userDataViewModel.uuid.value
        val token = userDataViewModel.userToken.value?.accessToken

        if (companyId == null || token == null) {
            Log.e("ServicesFragment", "companyId o token nulos, no se puede cargar servicios")
            return
        }

        binding.progressBarServices.visibility = View.VISIBLE
        binding.recyclerServices.visibility = View.GONE
        binding.textNoServices.visibility = View.GONE

        companyViewModel.getServicesByCompanyId(
            companyId = companyId,
            authHeader = "Bearer $token",
            page = 0,
            sortBy = "name",
            order = "asc"
        )
    }

    private fun observeServices() {
        companyViewModel.listOfServices.observe(viewLifecycleOwner) { response ->
            binding.progressBarServices.visibility = View.GONE

            if (response == null) return@observe

            if (response.isSuccessful) {
                val services = response.body()?.content ?: emptyList()
                if (services.isEmpty()) {
                    binding.textNoServices.visibility = View.VISIBLE
                    binding.recyclerServices.visibility = View.GONE
                } else {
                    binding.textNoServices.visibility = View.GONE
                    binding.recyclerServices.visibility = View.VISIBLE
                    adapter.setItems(services)
                }
            } else {
                if (response.code() == 401) {
                    showSessionExpiredDialog()
                } else {
                    Log.e("ServicesFragment", "Error al obtener servicios: ${response.code()}")
                    showError("Error al cargar los servicios (${response.code()})")
                }
            }
        }
    }

    private fun openAddEditService(service: GetServicesResponse?) {
        val companyId = userDataViewModel.uuid.value ?: return
        val token = userDataViewModel.userToken.value?.accessToken ?: return

        val intent = Intent(requireActivity(), AddEditServiceActivity::class.java).apply {
            putExtra(EXTRA_COMPANY_ID, companyId.toString())
            putExtra(EXTRA_AUTH_TOKEN, token)
            if (service != null) {
                putExtra(EXTRA_SERVICE_ID, service.serviceId.toString())
                putExtra(EXTRA_SERVICE_NAME, service.serviceName)
            }
        }
        startActivityForResult(intent, REQUEST_ADD_SERVICE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ADD_SERVICE && resultCode == android.app.Activity.RESULT_OK) {
            loadServices()
        }
    }

    private fun showSessionExpiredDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.sesion_expired_title))
            .setMessage(getString(R.string.sesion_expired_explication))
            .setPositiveButton(getString(R.string.dialog_ok)) { _, _ ->
                val intent = Intent(requireActivity(), LogInActivity::class.java)
                startActivity(intent)
            }.show()
    }

    private fun showError(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}
