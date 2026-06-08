package com.raj.slotify.activities.company

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.raj.slotify.databinding.ActivityAddEditServiceBinding
import com.raj.slotify.fragments.company.services.ServicesFragment.Companion.EXTRA_AUTH_HEADER
import com.raj.slotify.fragments.company.services.ServicesFragment.Companion.EXTRA_SERVICE_ID
import com.raj.slotify.viewModels.apiRest.ServiceViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel
import java.util.UUID

class AddEditServiceActivity : AppCompatActivity() {
    private val userDataViewModel: UserDataViewModel by viewModels()

    private val serviceViewModel: ServiceViewModel by viewModels()

    private lateinit var binding: ActivityAddEditServiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAddEditServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val serviceId = intent.getStringExtra(EXTRA_SERVICE_ID)
        val authHeader = intent.getStringExtra(EXTRA_AUTH_HEADER)

        if (serviceId != null && authHeader != null) {
            serviceViewModel.getServiceWithSchedules(UUID.fromString(serviceId), authHeader)
            Log.i("AddEditServiceActivity", "Cargando servicio con ID: $serviceId")
        }
    }
}
