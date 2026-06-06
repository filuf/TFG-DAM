package com.raj.slotify.activities.company

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.raj.slotify.R
import com.raj.slotify.databinding.ActivityAddEditServiceBinding
import com.raj.slotify.dtos.service.CreateServiceRequest
import com.raj.slotify.fragments.company.services.ServicesFragment.Companion.EXTRA_AUTH_TOKEN
import com.raj.slotify.fragments.company.services.ServicesFragment.Companion.EXTRA_SERVICE_NAME
import com.raj.slotify.viewModels.apiRest.ServiceViewModel

class AddEditServiceActivity : AppCompatActivity() {

    private val serviceViewModel: ServiceViewModel by viewModels()
    private lateinit var binding: ActivityAddEditServiceBinding

    private lateinit var authToken: String
    private var editMode: Boolean = false

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

        authToken = intent.getStringExtra(EXTRA_AUTH_TOKEN) ?: run {
            Log.e("AddEditServiceActivity", "No se recibió el token de autenticación")
            finish()
            return
        }

        val existingServiceName = intent.getStringExtra(EXTRA_SERVICE_NAME)
        editMode = existingServiceName != null

        setupToolbar()
        prefillIfEdit(existingServiceName)
        setupSaveButton()
        observeViewModel()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarAddService)
        binding.toolbarAddService.title = if (editMode) getString(R.string.edit_the_service) else getString(R.string.add_service)
        binding.toolbarAddService.setNavigationOnClickListener { finish() }
    }

    private fun prefillIfEdit(serviceName: String?) {
        if (serviceName != null) {
            binding.editServiceName.setText(serviceName)
        }
    }

    private fun setupSaveButton() {
        binding.btnSaveService.setOnClickListener {
            if (!validateForm()) return@setOnClickListener
            createService()
        }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val name = binding.editServiceName.text.toString().trim()
        if (name.isEmpty()) {
            binding.layoutServiceName.error = "El nombre es obligatorio"
            valid = false
        } else {
            binding.layoutServiceName.error = null
        }

        val durationStr = binding.editServiceDuration.text.toString().trim()
        val duration = durationStr.toIntOrNull()
        if (durationStr.isEmpty() || duration == null || duration <= 0) {
            binding.layoutServiceDuration.error = "Introduce una duración válida (en minutos)"
            valid = false
        } else {
            binding.layoutServiceDuration.error = null
        }

        val priceStr = binding.editServicePrice.text.toString().trim()
        val price = priceStr.toDoubleOrNull()
        if (priceStr.isEmpty() || price == null || price < 0) {
            binding.layoutServicePrice.error = "Introduce un precio válido"
            valid = false
        } else {
            binding.layoutServicePrice.error = null
        }

        val description = binding.editServiceDescription.text.toString().trim()
        if (description.isEmpty()) {
            binding.layoutServiceDescription.error = "La descripción es obligatoria"
            valid = false
        } else {
            binding.layoutServiceDescription.error = null
        }

        return valid
    }

    private fun createService() {
        val name = binding.editServiceName.text.toString().trim()
        val duration = binding.editServiceDuration.text.toString().trim().toInt()
        val priceEuros = binding.editServicePrice.text.toString().trim().toDouble()
        val priceCents = (priceEuros * 100).toInt()
        val description = binding.editServiceDescription.text.toString().trim()

        val request = CreateServiceRequest(
            serviceName = name,
            minutesDuration = duration,
            priceCent = priceCents,
            description = description
        )

        setLoading(true)
        serviceViewModel.createService("Bearer $authToken", request)
    }

    private fun observeViewModel() {
        serviceViewModel.serviceCreated.observe(this) { response ->
            if (response == null) return@observe
            setLoading(false)

            if (response.isSuccessful) {
                val createdService = response.body()
                Log.i("AddEditServiceActivity", "Servicio creado: ${createdService?.serviceName}")
                setResult(RESULT_OK)
                finish()
            } else {
                Log.e("AddEditServiceActivity", "Error al crear servicio: ${response.code()}")
                if (response.code() == 401) {
                    showError("Sesión expirada. Cierra sesión y vuelve a entrar.")
                } else {
                    showError("No se pudo crear el servicio (${response.code()}).")
                }
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBarAddService.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnSaveService.isEnabled = !loading
        binding.editServiceName.isEnabled = !loading
        binding.editServiceDuration.isEnabled = !loading
        binding.editServicePrice.isEnabled = !loading
        binding.editServiceDescription.isEnabled = !loading
    }

    private fun showError(message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}
