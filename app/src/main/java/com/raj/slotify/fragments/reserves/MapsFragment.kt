package com.raj.slotify.fragments.reserves

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.launch
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.raj.slotify.R
import com.raj.slotify.viewModels.frontend.EnterpriseRegistryViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapsFragment : Fragment() {

    private val userDataViewModel: UserDataViewModel by activityViewModels()
    private var mMap: GoogleMap? = null

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
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

        userDataViewModel.serviceLocation.observe(viewLifecycleOwner) { location ->
            viewLifecycleOwner.lifecycleScope.launch {
                loadMapFromAddress(location)
            }
        }
    }

    private suspend fun loadMapFromAddress(address: String) {
        if (!android.location.Geocoder.isPresent()) {
            Log.e("MapsFragment", "El dispositivo no tiene servicios de Geocodificación")
            return
        }

        val geocoder = android.location.Geocoder(requireContext())

        try {
            val addresses = geocoder.getFromLocationName(address, 1)

            if (!addresses.isNullOrEmpty()) {
                val addr = addresses[0]
                val location = LatLng(addr.latitude, addr.longitude)

                withContext(Dispatchers.Main) {
                    mMap?.apply {
                        clear()
                        addMarker(MarkerOptions().position(location).title(address))
                        animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MapsFragment", "Error buscando la dirección: ${e.message}")
        }
    }
}