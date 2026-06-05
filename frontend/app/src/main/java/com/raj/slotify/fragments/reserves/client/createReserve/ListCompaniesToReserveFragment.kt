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
import com.raj.slotify.models.TextModel
import com.raj.slotify.viewModels.apiRest.CompanyViewModel
import com.raj.slotify.viewModels.frontend.ClientReservesViewModel
import com.raj.slotify.viewModels.frontend.LayoutViewModel
import com.raj.slotify.viewModels.frontend.MainViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel
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
    private lateinit var testEnterprises: MutableList<GetCompanyResponse>

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
            testEnterprises = createTestEnterprises()

            listOfCompanies = testEnterprises
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
            listOfCompanies.addAll(testEnterprises)

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

    fun createTestEnterprises(): MutableList<GetCompanyResponse> {
        return mutableListOf(
            GetCompanyResponse(
                UUID.fromString("87ce0fc5-616c-4b69-8c58-c5b8557a1a1a"),
                3,
                "empresaMadrid",
                "645345843",
                "rodriYJesusDeLaManoEmpresa@emilio.com",
                "C. Monte Naranco, 10, Puente de Vallecas, 28053 Madrid",
                "",
                "descripcion muy bonita",
                "4.5",
                listOf()),
            GetCompanyResponse(
                UUID.fromString("0802f476-68c1-41e4-a5d6-81ca818a6725"),
                5,
                "empresaPueblo",
                "645345843",
                "rodriYJesusDeLaManoEmpresa@emilio.com",
                "C. Monte Naranco, 10, Puente de Vallecas, 28053 Madrid",
                "",
                "descripcion muy bonita",
                "4.5",
                listOf()
            )
        )
    }

}