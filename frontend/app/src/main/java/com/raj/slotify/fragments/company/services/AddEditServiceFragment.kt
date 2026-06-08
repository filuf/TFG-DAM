package com.raj.slotify.fragments.company.services

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.raj.slotify.R
import com.raj.slotify.databinding.FragmentAddEditServiceBinding
import com.raj.slotify.dtos.service.CreateServiceRequest
import com.raj.slotify.dtos.service.PatchServiceRequest
import com.raj.slotify.fragments.company.services.ServicesFragment.Companion.EXTRA_AUTH_HEADER
import com.raj.slotify.fragments.company.services.ServicesFragment.Companion.EXTRA_SERVICE_ID
import com.raj.slotify.tools.Formater.getMediaType
import com.raj.slotify.tools.Formater.toRequestBody
import com.raj.slotify.tools.Formater.uriToFile
import com.raj.slotify.tools.Verifier
import com.raj.slotify.viewModels.apiRest.ServiceViewModel
import com.raj.slotify.viewModels.frontend.EnterpriseDataViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.UUID
import kotlin.getValue

class AddEditServiceFragment : Fragment() {

    private val serviceViewModel: ServiceViewModel by activityViewModels()
    private val userDataViewModel: UserDataViewModel by activityViewModels()
    private val enterpriseDataViewModel: EnterpriseDataViewModel by activityViewModels()

    private lateinit var binding: FragmentAddEditServiceBinding
    private lateinit var authHeader : String
    private var editMode: Boolean = false
    private var serviceId: UUID? = null
    private var currentPhotoFile: File? = null
    private var imageFile: File? = null

    private var latestTmpUri: Uri? = null
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddEditServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as AppCompatActivity

        authHeader = activity.intent.getStringExtra(EXTRA_AUTH_HEADER) ?: run {
            Log.e("AddEditServiceActivity", "No se recibió el token de autenticación")
            activity.finish()
            return
        }

        Log.i("AddEditServiceActivity", "Token de autenticación recibido: $authHeader")

        userDataViewModel.setAuthHeader(authHeader)

        val idService = activity.intent.getStringExtra(EXTRA_SERVICE_ID)
        editMode = idService != null

        setupToolbar()
        setUpCardImage()
        setEditScheduleButton()
        prefillIfEdit(idService)
        setupSaveButton()

    }

    private fun setUpCardImage() {
        val cardImage: View = binding.cardImage
        cardImage.setOnClickListener {
            openImagePicker()
        }
    }

    private fun observeSchedules() {
        serviceViewModel.serviceWithSchedules.observe(viewLifecycleOwner) { serviceResponse ->
            if(serviceResponse == null ||
                !Verifier.verifySuccessfulResponse(
                    serviceResponse,
                    requireContext(),
                    positiveActionText = getString(R.string.dialog_ok)
                )
            ) return@observe

            val service = serviceResponse.body()!!

            serviceId = service.serviceId

            binding.editServiceName.setText(service.serviceName)
            binding.editServiceDuration.setText(service.serviceMinutesDuration.toString())
            binding.editServicePrice.setText((service.servicePriceCent/100).toString())
            binding.editServiceDescription.setText(service.description)

            enterpriseDataViewModel.setSchedulesOfService(service.schedules)

            binding.cardImage.visibility = View.VISIBLE
            val s3Url = service.s3ImageUrl // El link de AWS

            if (!s3Url.isNullOrEmpty()) {
                binding.imageService.load(s3Url) {
                    crossfade(true)
                    placeholder(R.drawable._logoslotify_retocado) // Pon un placeholder si tienes
                }
                binding.imageService.visibility = View.VISIBLE
                binding.selectImageText.visibility = View.GONE
            }

        }
    }

    private fun setEditScheduleButton() {
        binding.editSchedulesButton.setOnClickListener {
            try {
                // Esta es la forma más segura dentro de un Fragment
                findNavController().navigate(R.id.action_addEditServiceFragment_to_addEditSchedulesFragment)
            } catch (e: Exception) {
                Log.e("NavigationError", "No se pudo navegar: ${e.message}")
            }
        }
    }

    private fun setupToolbar() {
        val activity = requireActivity() as AppCompatActivity

        activity.setSupportActionBar(binding.toolbarAddService)
        binding.toolbarAddService.title = if (editMode) getString(R.string.edit_the_service) else getString(R.string.add_service)
        binding.toolbarAddService.setNavigationOnClickListener { activity.finish() }
    }

    private fun prefillIfEdit(serviceName: String?) {
        if (serviceName != null) {
            observeSchedules()
            binding.imageLayout.visibility = View.VISIBLE
            binding.editSchedulesButton.visibility = View.VISIBLE
        } else {
            binding.imageLayout.visibility = View.GONE
            binding.editSchedulesButton.visibility = View.GONE
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

        return valid
    }

    private fun setupSaveButton() {
        binding.btnSaveService.setOnClickListener {
            if (!validateForm()) return@setOnClickListener
            if (editMode) {
                patchService()
            } else {
                createService()
            }
        }
    }

    private fun patchService() {
        val name = binding.editServiceName.text.toString().trim()
        val duration = binding.editServiceDuration.text.toString().trim().toInt()
        val priceEuros = binding.editServicePrice.text.toString().trim().toDouble()
        val priceCents = (priceEuros * 100).toInt()
        val description = binding.editServiceDescription.text.toString().trim()

        val patchRequest = PatchServiceRequest(
            serviceName = name,
            minutesDuration = duration,
            priceCent = priceCents,
            description = description
        )

        // LÓGICA PARA LA IMAGEN (Igual que en patchService)
        var filePart: MultipartBody.Part? = null
        imageFile?.let { file ->
            val mediaType = imageUri?.getMediaType(requireContext()) ?: "image/jpeg".toMediaTypeOrNull()
            val requestFile = file.asRequestBody(mediaType)
            filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
        }
        val request = patchRequest.toRequestBody()

        serviceViewModel.patchService(
            serviceId = serviceId!!,
            authHeader = authHeader,
            file = filePart,
            patchServiceRequest = request
        )
        setLoading(true)
        observeServicePatched()
    }

    private fun observeServicePatched() {
        serviceViewModel.servicePatched.observe(viewLifecycleOwner) { response ->
            if (response == null) return@observe

            if( !Verifier.verifySuccessfulResponse(
                response,
                requireContext(),
                positiveActionText = getString(R.string.dialog_ok)
            )) {
                setLoading(false)
                Log.e("AddEditServiceActivity", "Error al actualizar el servicio: ${response.code()}")

                serviceViewModel.servicePatched.removeObservers(viewLifecycleOwner)
            } else {
                setLoading(false)
                val serviceUpdated = response.body()!!

                Log.i("AddEditServiceActivity", "Servicio actualizado: $serviceUpdated")

                val activity = requireActivity() as AppCompatActivity

                activity.setResult(RESULT_OK)
                activity.finish()

                serviceViewModel.servicePatched.removeObservers(viewLifecycleOwner)
            }
        }
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

        serviceViewModel.createService(authHeader, request)
        observeServiceCreated()
        setLoading(true)
    }

    private fun observeServiceCreated() {
        serviceViewModel.serviceCreated.observe(viewLifecycleOwner) { response ->
            if (response == null) return@observe

            if(!Verifier.verifySuccessfulResponse(
                response,
                requireContext(),
                positiveActionText = getString(R.string.dialog_ok)
            )) {
                setLoading(false)
                Log.e("AddEditServiceActivity", "Error al crear el servicio: ${response.code()}")

                serviceViewModel.serviceCreated.removeObservers(viewLifecycleOwner)
            } else {
                val createdService = response.body()!!
                Log.i("AddEditServiceActivity", "Servicio creado: $createdService")
                setLoading(false)

                val activity = requireActivity() as AppCompatActivity

                activity.setResult(RESULT_OK)
                activity.finish()

                serviceViewModel.serviceCreated.removeObservers(viewLifecycleOwner)
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
        binding.cardImage.isEnabled = !loading
        binding.editSchedulesButton.isEnabled = !loading
    }

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("temp_image", ".jpg", requireContext().cacheDir)
        currentPhotoFile = tmpFile
        return FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            tmpFile
        )
    }

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val galleryUri = result.data?.data
            val imageView: ImageView = binding.imageService
            val textSelect = binding.selectImageText

            if (galleryUri != null) {
                // CASO GALERÍA
                imageUri = galleryUri
                imageFile = galleryUri.uriToFile(requireContext())

                imageView.setImageURI(galleryUri)
            } else {
                // CASO CÁMARA
                currentPhotoFile?.let { file ->
                    if (file.exists()) {
                        imageFile = file
                        imageUri = latestTmpUri // Guardamos la URI para el MediaType

                        imageView.setImageURI(latestTmpUri)
                    }
                }
            }

            // Actualizar visibilidad común
            if (imageFile != null) {
                textSelect.visibility = View.GONE
                imageView.visibility = View.VISIBLE
            }
        }
    }

    private fun openImagePicker() {
        // ** IMPORTANT ** GENERATE THE URI BEFORE THE INTENT AND SAVE IT
        latestTmpUri = getTmpFileUri()

        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // SPECIFY WHERE THE FULL QUALITY PHOTO WILL BE SAVED
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, latestTmpUri)

        val getContentIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }

        val chooserIntent = Intent.createChooser(getContentIntent, "Selecciona imagen vía")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(captureIntent))

        imagePickerLauncher.launch(chooserIntent)
    }

}