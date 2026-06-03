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
import com.raj.slotify.R
import com.raj.slotify.adapters.CompaniesListAdapter
import com.raj.slotify.databinding.FragmentListCompaniesToReserveBinding
import com.raj.slotify.dtos.company.GetCompanyResponse
import com.raj.slotify.dtos.service.ScheduleSummary
import com.raj.slotify.dtos.service.ServiceSummary
import com.raj.slotify.models.TextModel
import com.raj.slotify.viewModels.apiRest.CompanyViewModel
import com.raj.slotify.viewModels.frontend.ClientReservesViewModel
import com.raj.slotify.viewModels.frontend.LayoutViewModel
import com.raj.slotify.viewModels.frontend.MainViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel
import java.time.LocalTime
import java.util.UUID
import kotlin.getValue

class ListCompaniesToReserveFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val layoutViewModel: LayoutViewModel by activityViewModels()
    private val userDataViewModel: UserDataViewModel by activityViewModels()
    private val clientReservesViewModel: ClientReservesViewModel by activityViewModels()
    private val companyViewModel: CompanyViewModel by activityViewModels()
    private lateinit var listOfCompanies: MutableList<GetCompanyResponse>
    private lateinit var binding: FragmentListCompaniesToReserveBinding
    private lateinit var testEnterprise: GetCompanyResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainViewModel.setSubtitle(TextModel(R.string.site_where_reserve))

        layoutViewModel.setExplicationVisibility(View.GONE)
        layoutViewModel.setBackButtonVisibility(View.VISIBLE)

        binding = FragmentListCompaniesToReserveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: IMPLEMENTAR LA PETICION REAL A LA API
        // companyViewModel.getCompanies("Bearer ${userDataViewModel.userToken.value?.accessToken ?: ""}")

        if (!::listOfCompanies.isInitialized) {
            testEnterprise = createTestEnterprise()
            listOfCompanies = mutableListOf(testEnterprise)
        }

        val adapter = setUpRecycler(view)
        configSearchBar(adapter)
        listenToCompanies(adapter)
    }

    fun listenToCompanies(adapter: CompaniesListAdapter) {
        companyViewModel.companies.observe(viewLifecycleOwner) { response ->
            // DISCARD UNSUCCESSFULLY CASES
            if (response == null)
                return@observe

            if (!response.isSuccessful) {
                Log.e("ViewAllReserves", "Error cargando empresas: ${response.code()}")
                return@observe
            }
            if (response.body() == null)
                return@observe

            val responseBody = response.body()!!

            listOfCompanies.clear()
            listOfCompanies.add(testEnterprise)

            // TODO: DESCOMENTAR ESTO CUANDO SE HAGA LA PETICION REAL
            // listOfCompanies.addAll(responseBody.content)

            adapter.setItems(listOfCompanies)
        }
    }

    fun configSearchBar(adapter: CompaniesListAdapter) {
        binding.searchEnterpriseText.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty()) {
                adapter.setItems(listOfCompanies)
            } else {
                val filteredList = listOfCompanies
                    .filter { company ->
                        company.companyName.lowercase().contains(text.toString().lowercase().trim())
                    }
                    .toMutableList()

                adapter.setItems(filteredList)
            }
        }
    }

    fun setUpRecycler(view: View): CompaniesListAdapter {
        val recyclerView = binding.companiesRecycler
        val customAdapter = CompaniesListAdapter(listOfCompanies) { enterprise ->
            clientReservesViewModel.setCompanyToReserve(enterprise)
            view.findNavController().navigate(R.id.action_listCompaniesToReserveFragment_to_listCompanyServicesFragment)
        }
        recyclerView.adapter = customAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        return customAdapter
    }

    fun createTestEnterprise(): GetCompanyResponse {
        return GetCompanyResponse(
            UUID.fromString("87ce0fc5-616c-4b69-8c58-c5b8557a1a1a"),
            3,
            "empresa",
            "645345843",
            "rodriYJesusDeLaManoEmpresa@emilio.com",
            "C. Monte Naranco, 10, Puente de Vallecas, 28053 Madrid",
            "",
            "descripcion muy bonita",
            "4.5",
            listOf(ServiceSummary(
                UUID.fromString("82cdc47d-9f38-4ff2-9614-a9dc3f72faf4"),
                "miServicio90",
                50,
                30,
                "",
                "servicio muy interesente",
                listOf(ScheduleSummary(
                    UUID.fromString("87ce0fc5-616c-4b69-8c58-c5b8557a1a1b"),
                    1,
                    LocalTime.of(14, 30),
                    LocalTime.of(22, 30)
                ))
            ))
        )
    }

}