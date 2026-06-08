package com.raj.slotify.fragments.editUserData.company

import android.app.Activity
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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import coil.load
import com.hbb20.CountryCodePicker
import com.raj.slotify.R
import com.raj.slotify.databinding.FragmentCompanyEditDataBinding
import com.raj.slotify.dtos.company.PatchCompanyRequest
import com.raj.slotify.models.TextModel
import com.raj.slotify.tools.Formater.getMediaType
import com.raj.slotify.tools.Formater.toRequestBody
import com.raj.slotify.tools.Formater.uriToFile
import com.raj.slotify.tools.Verifier
import com.raj.slotify.viewModels.apiRest.CompanyViewModel
import com.raj.slotify.viewModels.frontend.EnterpriseDataViewModel
import com.raj.slotify.viewModels.frontend.LayoutViewModel
import com.raj.slotify.viewModels.frontend.MainViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel
import com.raj.slotify.viewModels.room.TokenViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class CompanyEditDataFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val layoutViewModel: LayoutViewModel by activityViewModels()
    private val userDataViewModel: UserDataViewModel by activityViewModels()
    private val tokenViewModel: TokenViewModel by activityViewModels()
    private val enterpriseDataViewModel: EnterpriseDataViewModel by activityViewModels()
    private val companyViewModel: CompanyViewModel by activityViewModels()

    private var latestTmpUri: Uri? = null
    private var imageUri: Uri? = null
    private var simultaneousServices: Int? = null

    private lateinit var binding: FragmentCompanyEditDataBinding
    private lateinit var authHeader: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainViewModel.setTitle(TextModel(R.string.modify_company_data))

        binding = FragmentCompanyEditDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutViewModel.setCancelButtonVisibility(View.VISIBLE)
        layoutViewModel.setBackButtonVisibility(View.GONE)

        getTokenAndData()

        listenEnterpriseData()

        val cardImage: View = binding.cardImage

        cardImage.setOnClickListener {
            openImagePicker()
        }

        observeCompanyDirection()
        setCompanyDirectionButton()
        configNumberPicker()

        val ccp = setCcpPicker()
        setConfirmButtonClicked(ccp)

    }

    private fun observeCompanyDirection() {
        userDataViewModel.serviceLocation.observe(viewLifecycleOwner) { location ->
            if (!location.isNullOrEmpty()) {
                binding.selectedLocationText.visibility = View.VISIBLE
                Log.i("CompanyEditDataFragment", "Actualizando UI con dirección: $location")
                binding.selectedLocationText.text = "${getString(R.string.selected_direction)} $location"
            } else {
                binding.selectedLocationText.visibility = View.GONE
            }
        }
    }

    private fun getTokenAndData() {
        tokenViewModel.token.observe(viewLifecycleOwner) { token ->
            if (token != null) {
                Log.i("MainActivity", "Token obtenido de Room: ${token.accessToken}")
                authHeader = "Bearer ${token.accessToken}"
                getCompany(authHeader)
            }
        }
    }

    private fun getCompany(authHeader: String) {
        userDataViewModel.uuid.observe(viewLifecycleOwner) { uuid ->
            if (uuid != null) {
                Log.i("CompanyEditDataFragment", "UUID obtenido: $uuid, authHeader: $authHeader")
                companyViewModel.getCompanyById(uuid, authHeader)
            }
        }
    }

    private fun listenEnterpriseData() {
        companyViewModel.company.observe(viewLifecycleOwner) { response ->
            if (response == null) return@observe

            if (!Verifier.verifySuccessfulResponse(
                    response,
                    requireContext(),
                    positiveActionText = getString(R.string.dialog_ok)
                )) {
                return@observe
            }

            Log.i("CompanyEditDataFragment", "Respuesta exitosa recibida: ${response.body()}")

            val companyData = response.body()!!

            binding.editCompanyNameText.setText(companyData.companyName)
            binding.editCompanyDescriptionText.setText(companyData.description)
            binding.companyPhoneInput.setText(companyData.phoneNumber.toString())

            val s3Key = companyData.s3ImageUrl
            if (!s3Key.isNullOrEmpty()) {
                binding.editCompanyImage.load(s3Key) {
                    crossfade(true)
                    placeholder(R.drawable._logoslotify_retocado) // Pon un placeholder si tienes
                }
                binding.editCompanyImage.visibility = View.VISIBLE
                binding.selectImageText.visibility = View.GONE
            }

            // DIRECTION
            val addressFromApi = companyData.physicalAddress

            if (userDataViewModel.serviceLocation.value.isNullOrEmpty()) {
                userDataViewModel.setServiceLocation(addressFromApi)
            }

            // NumberPicker
            val concurrentServices = companyData.defaultMaxConcurrentServices
            binding.simultaneousServicesNumberPicker.value = concurrentServices
            simultaneousServices = concurrentServices
        }
    }

    private fun setCompanyDirectionButton() {
        val button = binding.editLocationButton
        button.setOnClickListener {
            layoutViewModel.setCancelButtonVisibility(View.GONE)
            layoutViewModel.setBackButtonVisibility(View.VISIBLE)

            view?.findNavController()?.navigate(R.id.action_companyEditDataFragment_to_selectPhysicalDirectionFragment2)
        }
    }

    private fun setCcpPicker(): CountryCodePicker {
        val ccp = binding.editCompanyCcp
        val phoneInput = binding.companyPhoneInput
        ccp.registerCarrierNumberEditText(phoneInput)
        return ccp
    }

    private fun setConfirmButtonClicked(ccp: CountryCodePicker) {
        viewLifecycleOwner.lifecycleScope.launch {
            layoutViewModel.confirmButtonClicked.collect {

                val enterpriseName = binding.editCompanyNameText.text
                val description = binding.editCompanyDescriptionText.text

                val phone: String = binding.companyPhoneInput.text.toString().filter { it.isDigit() }
                val isValid = ccp.isValidFullNumber

                var valid = true

                if (phone.isEmpty()) {
                    binding.companyPhoneLayout.error = null
                }
                else if (!isValid) {
                    binding.companyPhoneLayout.error = getString(R.string.phone_invalid_message)
                    valid = false
                } else {
                    binding.companyPhoneLayout.error = null
                }

                if (enterpriseName.isNullOrEmpty()) {
                    binding.editCompanyNameLayout.error = getString(R.string.user_name_empty)
                    valid = false
                } else {
                    binding.editCompanyNameLayout.error = null
                }

                if (simultaneousServices == null || simultaneousServices == 0) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.enter_simultaneous_services),
                        Toast.LENGTH_SHORT
                    ).show()
                    valid = false
                }

                if (valid) {
                    userDataViewModel.setPhone(phone)
                    userDataViewModel.setName(enterpriseName.toString().trim())

                    if(!description.isNullOrEmpty()) {
                        enterpriseDataViewModel.setDescription(description.toString().trim())
                    }

                    enterpriseDataViewModel.setConcurrentServices(simultaneousServices!!)

                    imageUri?.let { userDataViewModel.setImageUri(it) }

                    var imageFile: File? = null
                    if (imageUri != null) {
                        imageFile = imageUri!!.uriToFile(requireContext())!!
                    }

                    val patchCompanyRequest = PatchCompanyRequest(
                        simultaneousServices!!,
                        phone,
                        userDataViewModel.serviceLocation.value,
                        enterpriseDataViewModel.description.value
                    )

                    uploadCompanyData(imageFile, patchCompanyRequest)
                }
            }
        }
    }

    private fun uploadCompanyData(imageFile: File?, patchCompanyRequest: PatchCompanyRequest) {
        var filePart: MultipartBody.Part? = null

        imageFile?.let { file ->
            val mediaType = imageUri?.getMediaType(requireContext())
                ?: "image/jpeg".toMediaTypeOrNull()

            val requestFile = file.asRequestBody(mediaType)

            filePart = MultipartBody.Part.createFormData(
                "file",
                file.name,
                requestFile
            )
        }
        val request = patchCompanyRequest.toRequestBody()

        companyViewModel.patchCompany(authHeader, filePart, request)
        listenCompanyUpdated()
    }

    private fun listenCompanyUpdated() {
        companyViewModel.patchedCompany.observe(viewLifecycleOwner) { response ->
            if (response == null) {
                binding.editCompanyLayout.visibility = View.VISIBLE
                binding.companyDataProgressBar.visibility = View.GONE

                return@observe
            }
            if (!Verifier.verifySuccessfulResponse(response, requireContext(), positiveActionText = getString(R.string.dialog_ok))) {
                binding.editCompanyLayout.visibility = View.VISIBLE
                binding.companyDataProgressBar.visibility = View.GONE

                return@observe
            }

            val company = response.body()!!
            Log.i("CompanyEditDataFragment", "Company: $company")
            requireActivity().finish()
        }
    }

    private fun configNumberPicker() {
        val numberPicker = binding.simultaneousServicesNumberPicker
        // SET THE MINIMUM AND MAXIMUM VALUES FOR THE PICKER
        numberPicker.minValue = 1
        numberPicker.maxValue = 100

        // SET THE INITIAL VALUE TO BE DISPLAYED
        numberPicker.value = 1

        // ENABLE OR DISABLE WRAPPING AROUND THE VALUES
        numberPicker.wrapSelectorWheel = true

        // CAPTURE THE VALUE CHANGE EVENT TO RESPOND TO USER INPUT
        numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            // LOG OR USE THE NEW SELECTED NUMBER
            simultaneousServices = newVal
        }
    }

    private fun getTmpFileUri(): Uri {
        // CREATES TEMPORAL FILE IN CACHE DIRECTORY.
        val tmpFile = File.createTempFile("temp_image", ".jpg", requireContext().cacheDir).apply {
            createNewFile() // Assures that the file exists
        }
        // CONVERT FILE IN SECURE URI USING FILE PROVIDER.
        return FileProvider.getUriForFile(requireContext().applicationContext, "${requireContext().packageName}.provider", tmpFile)
    }

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            // TRIES TO GET THE URI FROM THE GALLERY.
            val galleryUri = result.data?.data
            val textSelect = binding.selectImageText

            val imageView: ImageView = binding.editCompanyImage

            if (galleryUri != null) {
                // GALLERY CASE.
                // USER SELECT AN EXISTING IMAGE.

                imageView.setImageURI(galleryUri)

                textSelect.visibility = View.GONE
                imageView.visibility = View.VISIBLE

                imageUri = galleryUri
            } else {
                // CAMERA CASE.
                // IF galleryUri IS NULL, IS BECAUSE THE CAMERA WAS USED AND HAS NOT BEEN SAVED
                // THE PHOTO IN THE URI extraOutput

                latestTmpUri?.let { cameraUri ->
                    // ASSURES THAT THE FILE IS CREATED
                    val file = File(requireContext().cacheDir, cameraUri.lastPathSegment ?: "")
                    if (file.exists()) {
                        imageView.setImageURI(cameraUri)
                        textSelect.visibility = View.GONE
                        imageView.visibility = View.VISIBLE
                        imageUri = cameraUri
                    } else {
                        imageView.visibility = View.GONE
                        textSelect.visibility = View.VISIBLE
                    }
                }
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