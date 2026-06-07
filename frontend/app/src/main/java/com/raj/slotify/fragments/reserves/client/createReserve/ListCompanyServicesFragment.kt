package com.raj.slotify.fragments.reserves.client.createReserve

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raj.slotify.R
import com.raj.slotify.adapters.CompanyServicesAdapter
import com.raj.slotify.databinding.FragmentListCompanyServicesBinding
import com.raj.slotify.dtos.company.GetServicesResponse
import com.raj.slotify.dtos.company.SearchCompaniesResponse
import com.raj.slotify.models.TextModel
import com.raj.slotify.tools.Verifier
import com.raj.slotify.viewModels.apiRest.CompanyViewModel
import com.raj.slotify.viewModels.frontend.ClientReservesViewModel
import com.raj.slotify.viewModels.frontend.LayoutViewModel
import com.raj.slotify.viewModels.frontend.MainViewModel
import com.raj.slotify.viewModels.room.TokenViewModel
import java.util.UUID
import kotlin.getValue

class ListCompanyServicesFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val layoutViewModel: LayoutViewModel by activityViewModels()
    private val tokenViewModel: TokenViewModel by activityViewModels()
    private val clientReservesViewModel: ClientReservesViewModel by activityViewModels()
    private val companyViewModel: CompanyViewModel by activityViewModels()

    private var isLoading = false
    private var actualPage: Int = 0
    private var totalPages: Int = 0
    private lateinit var binding: FragmentListCompanyServicesBinding
    private lateinit var company: SearchCompaniesResponse
    private lateinit var services: List<GetServicesResponse>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainViewModel.setSubtitle(TextModel(R.string.service_to_reserve_question))
        layoutViewModel.setExplicationVisibility(View.VISIBLE)

        binding = FragmentListCompanyServicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter: CompanyServicesAdapter = setUpRecycler()

        configSearchBar(adapter)
        observeCompanies()
        observeServices(adapter)
    }

    fun configSearchBar(adapter: CompanyServicesAdapter) {
        binding.searchServiceReserveText.doOnTextChanged { text, _, _, _ ->

            if (text.isNullOrEmpty()) {
                adapter.setItems(services)
            } else {
                val filteredList = services
                    .filter { service ->
                        service.serviceName.lowercase().contains(text.toString().lowercase().trim())
                    }
                    .toMutableList()

                adapter.setItems(filteredList)
            }
        }
    }

    fun observeServices(adapter: CompanyServicesAdapter) {
        companyViewModel.listOfServices.observe(viewLifecycleOwner) { response ->
            if (response == null || !Verifier.verifySuccessfulResponse(
                response,
                requireContext(),
                positiveAction = {view?.findNavController()?.popBackStack()},
                positiveActionText = getString(R.string.dialog_retry)
            )) {
                return@observe
            }

            val responseBody = response.body()!!
            actualPage = responseBody.number
            totalPages = responseBody.totalPages

            val services = responseBody.content
            Log.i("Services", services.toString())

            this.services = services

            adapter.setItems(services)
        }

    }

    fun getServices(companyId: UUID, page: Int = 0) {
        tokenViewModel.token.observe(viewLifecycleOwner) { tokenEntity ->
            if (tokenEntity == null)
                return@observe

            val authHeader = "Bearer ${tokenEntity.accessToken}"
            Log.i("AuthHeader en getServices", authHeader)

            companyViewModel.getServicesByCompanyId(companyId, authHeader, page = page)
        }
    }

    fun observeCompanies() {
        clientReservesViewModel.companyToReserve.observe(viewLifecycleOwner) { company ->
            mainViewModel.setExplication(TextModel(
                customText = "${getString(R.string.in_word)} ${company.companyName}"
            ))

            // GET SERVICES
            this.company = company
            getServices(company.companyId)
        }
    }

    private fun loadNextPage() {
        isLoading = true
        val next = actualPage + 1
        getServices(company.companyId,next)
    }

    fun setUpRecycler(): CompanyServicesAdapter {
        val recyclerView = binding.recyclerListServices
        val customAdapter = CompanyServicesAdapter(mutableListOf()) { service ->
            clientReservesViewModel.setServiceToReserve(service)
            view?.findNavController()?.navigate(R.id.action_listCompanyServicesFragment_to_selectReserveDetailsFragment)
        }
        recyclerView.adapter = customAdapter

        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (!isLoading && lastVisibleItemPosition == totalItemCount - 1) {
                    if (actualPage < totalPages - 1) {
                        loadNextPage()
                    }
                }
            }
        })

        return customAdapter
    }

}