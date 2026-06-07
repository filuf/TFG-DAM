package com.raj.slotify.fragments.registry.enterpriseRegistry

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raj.slotify.R
import com.raj.slotify.databinding.FragmentSelectPhysicalDirectionBinding
import com.raj.slotify.dtos.maps.PlaceSuggestion
import com.raj.slotify.adapters.SuggestionAdapter
import com.raj.slotify.viewModels.frontend.EnterpriseDataViewModel
import com.raj.slotify.viewModels.frontend.LayoutViewModel
import com.raj.slotify.viewModels.frontend.MainViewModel
import com.raj.slotify.viewModels.frontend.MapViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.raj.slotify.models.TextModel
import kotlinx.coroutines.launch

class SelectPhysicalDirectionFragment : Fragment(), OnMapReadyCallback {

    private val viewModel: MainViewModel by activityViewModels()
    private val userViewModel: UserDataViewModel by activityViewModels()
    private val layoutViewModel: LayoutViewModel by activityViewModels()
    private val enterpriseViewModel: EnterpriseDataViewModel by activityViewModels()
    private val mapViewModel: MapViewModel by activityViewModels()

    private lateinit var binding: FragmentSelectPhysicalDirectionBinding

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private var placeSuggestion: PlaceSuggestion? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.setTitle(TextModel(R.string.establish_enterprise_ubication))

        binding = FragmentSelectPhysicalDirectionBinding.inflate(inflater, container, false)

        mapView = binding.mapView

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // VARIABLES
        val cardSuggestion = binding.cardSuggestion
        val layoutMap = binding.layoutMap
        val imageSpin = binding.imageSpin

        // SET ANIMATION
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate)

        // SET UP VISIBILITY
        cardSuggestion.visibility = View.GONE
        layoutMap.visibility = View.GONE
        imageSpin.visibility = View.GONE

        mapView.visibility = View.GONE
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // LISTEN TEX OF DIRECTION INPUT AND DO THE PETITION TO DIRECTION VERIFICATION API TO SEARCH CORDS
        val textUbication = binding.textInputUbication

        // SET THE TEXT OF THE VIEWMODEL IF EXITS
        var textUbicationViewModel: String? = enterpriseViewModel.ubicationPlaceSuggestion.value?.displayName
        textUbicationViewModel = userViewModel.serviceLocation.value?: textUbicationViewModel

        if (textUbicationViewModel != null) {
            textUbication.setText(textUbicationViewModel)
        }

        textUbication.doOnTextChanged { text, _, _, _ ->
            val textQuery = text.toString().trim()

            if (textQuery.length < 3) {
                cardSuggestion.visibility = View.GONE
            } else {
                cardSuggestion.visibility = View.VISIBLE
                mapViewModel.searchPlaces(
                    textQuery,
                    "jsonv2",
                    "Slotify/0.1 (slotify.customer.support@gmail.com)"
                )
            }
        }

        // RECYCLER WITH SUGGESTIONS, WITH A CLICK LISTENER THAT CHARGES THE GOOGLE MAP
        val recycler: RecyclerView = binding.suggestionFragment
        val suggestionAdapter = SuggestionAdapter { placeSuggestion: PlaceSuggestion? ->
            this.placeSuggestion = placeSuggestion

            textUbication.setText(placeSuggestion?.displayName)

            layoutMap.visibility = View.VISIBLE

            cardSuggestion.visibility = View.GONE
            imageSpin.visibility = View.VISIBLE

            imageSpin.startAnimation(animation)

            val lat = placeSuggestion?.lat
            val long = placeSuggestion?.lon

            val site = LatLng(lat!!, long!!)
            val zoomLevel = 15f

            googleMap.addMarker(MarkerOptions().position(site).title(userViewModel.name.value))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(site, zoomLevel))

            imageSpin.visibility = View.GONE
            mapView.visibility = View.VISIBLE
        }

        recycler.adapter = suggestionAdapter
        recycler.layoutManager = LinearLayoutManager(requireContext())

        mapViewModel.placeSuggestions.observe(viewLifecycleOwner) {suggestions ->
            suggestionAdapter.submitList(suggestions)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            layoutViewModel.nextButtonClicked.collect {
                enterpriseViewModel.setUbicationCords(placeSuggestion)
                view.findNavController().navigate(R.id.action_selectPhysicalDirectionFragment_to_selectConcurrentServicesFragment)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            layoutViewModel.confirmButtonClicked.collect {
                userViewModel.setServiceLocation(textUbication.text.toString())

                Log.i("TextUbication", textUbication.text.toString())

                view.findNavController().popBackStack(R.id.companyEditDataFragment, false)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            layoutViewModel.backButtonClicked.collect {
                view.findNavController().popBackStack()
            }
        }
    }

    // MAP LIFE CYCLE
    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        googleMap.setOnMapClickListener { latLng ->
            // CONSTRUCT THE GOOGLE MAPS SEARCH API URL WITH LATITUDE AND LONGITUDE
            val direction: Uri? = placeSuggestion?.displayName?.toUri()
            val urlString = "https://www.google.com/maps/search/?api=1&query=${direction}"

            // PARSE THE STRING URL INTO A URI OBJECT AND CREATE THE VIEW INTENT
            val mapIntent = Intent(Intent.ACTION_VIEW, urlString.toUri())

            // FORCE THE INTENT TO OPEN THE OFFICIAL GOOGLE MAPS APPLICATION
            mapIntent.setPackage("com.google.android.apps.maps")

            try {
                // ATTEMPT TO START THE MAPS ACTIVITY
                startActivity(mapIntent)
            } catch (e: Exception) {
                // FALLBACK TO THE WEB BROWSER IF THE GOOGLE MAPS APP IS NOT INSTALLED
                Log.i("MAP INTENT", "OPENING GOOGLE MAPS WEB: $e")
                val browserIntent = Intent(Intent.ACTION_VIEW, urlString.toUri())
                startActivity(browserIntent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}