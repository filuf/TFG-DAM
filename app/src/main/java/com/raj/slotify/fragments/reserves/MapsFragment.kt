package com.raj.slotify.fragments.reserves

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.raj.slotify.R
import com.raj.slotify.viewModels.frontend.EnterpriseRegistryViewModel

class MapsFragment : Fragment() {

    private val enterpriseViewModel: EnterpriseRegistryViewModel by activityViewModels()
    private var mMap: GoogleMap? = null

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        // Iniciamos la observación una vez que el mapa está listo
        setupPlaceObserver()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun setupPlaceObserver() {
        // Observamos los cambios en la sugerencia del lugar
        enterpriseViewModel.ubicationPlaceSuggestion.observe(viewLifecycleOwner) { placeSuggestion ->
            placeSuggestion?.let { suggestion ->
                val lat = suggestion.lat
                val lon = suggestion.lon
                val location = LatLng(lat, lon)

                mMap?.apply {
                    clear()
                    addMarker(
                        MarkerOptions()
                        .position(location)
                        .title(suggestion.displayName))

                    // Animamos la cámara hacia la nueva ubicación
                    animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
                }
            }
        }
    }
}