package com.raj.slotify.fragments.editUserData.client

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hbb20.CountryCodePicker
import com.raj.slotify.R
import com.raj.slotify.activities.MainActivity
import com.raj.slotify.databinding.FragmentClientEditDataBinding
import com.raj.slotify.dtos.user.PatchUserRequest
import com.raj.slotify.models.TextModel
import com.raj.slotify.tools.Formater.getMediaType
import com.raj.slotify.tools.Formater.toRequestBody
import com.raj.slotify.tools.Formater.uriToFile
import com.raj.slotify.tools.Verifier
import com.raj.slotify.viewModels.apiRest.UserViewModel
import com.raj.slotify.viewModels.frontend.LayoutViewModel
import com.raj.slotify.viewModels.frontend.MainViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel
import com.raj.slotify.viewModels.room.TokenViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ClientEditDataFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val userDataViewModel: UserDataViewModel by activityViewModels()
    private val layoutViewModel: LayoutViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val tokenViewModel: TokenViewModel by activityViewModels()

    private lateinit var binding: FragmentClientEditDataBinding
    private lateinit var authHeader: String
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
        viewModel.setTitle(TextModel(R.string.modify_user_data))

        binding = FragmentClientEditDataBinding.inflate(inflater, container, false)
        return binding.root
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

            val imageView: ImageView = binding.imageUser

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getUserTokenAndData()

        observeClientData()
        val cardImage: View = binding.cardImage

        cardImage.setOnClickListener {
            openImagePicker()
        }

        val phoneInput = binding.phoneInput

        val ccp = binding.ccp
        ccp.registerCarrierNumberEditText(phoneInput)

        configConfirmButton(ccp)
    }

    private fun getUserTokenAndData() {
        tokenViewModel.token.observe(viewLifecycleOwner) { token ->
            if (token != null) {
                Log.i("MainActivity", "Token obtenido de Room: ${token.accessToken}")
                authHeader = "Bearer ${token.accessToken}"
                getUser(authHeader)
            }
        }
    }

    private fun getUser(authHeader: String) {
        userDataViewModel.uuid.observe(viewLifecycleOwner) { uuid ->
            if (uuid == null)
                return@observe

            Log.i("ClientEditDataFragment", "UUID obtenido: $uuid, authHeader: $authHeader")
            userViewModel.getUser(uuid, authHeader)
        }
    }

    private fun observeClientData() {
        userViewModel.userSearched.observe(viewLifecycleOwner) { user ->
            if (user == null)
                return@observe

            if(!Verifier.verifySuccessfulResponse(
                user,
                requireContext(),
                positiveActionText = getString(R.string.dialog_ok)
            )) {
                return@observe
            }

            val user = user.body()!!

            Log.i("ClientEditDataFragment", "User: $user")

            binding.userNameText.setText(user.username)
            binding.phoneInput.setText(user.phoneNumber)

            val s3Url = user.s3ImageUrl // El link de AWS

            if (!s3Url.isNullOrEmpty()) {
                binding.imageUser.load(s3Url) {
                    crossfade(true)
                    placeholder(R.drawable._logoslotify_retocado) // Pon un placeholder si tienes
                }
                binding.imageUser.visibility = View.VISIBLE
                binding.selectImageText.visibility = View.GONE
            }
        }

    }

    fun uploadUserData(imageFile: File?, patchUserRequest: PatchUserRequest) {
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
        val request = patchUserRequest.toRequestBody()

        binding.editClientDataLayout.visibility = View.GONE
        binding.editClientProgressBar.visibility = View.VISIBLE

        userViewModel.patchUser(authHeader, filePart, request)
        listenUploadResponse()
    }

    private fun listenUploadResponse() {
        userViewModel.userUpdated.observe(viewLifecycleOwner) { response ->
            if (response == null) {
                binding.editClientDataLayout.visibility = View.VISIBLE
                binding.editClientProgressBar.visibility = View.GONE

                return@observe
            }
            if (!Verifier.verifySuccessfulResponse(response, requireContext(), positiveActionText = getString(R.string.dialog_ok))) {
                binding.editClientDataLayout.visibility = View.VISIBLE
                binding.editClientProgressBar.visibility = View.GONE

                return@observe
            }

            val user = response.body()!!
            Log.i("ClientEditDataFragment", "User: $user")

            binding.editClientProgressBar.visibility = View.GONE
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.data_changued))
                .setPositiveButton(getString(R.string.dialog_ok),null)
                .setOnDismissListener {
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                .show()
        }
    }

    private fun configConfirmButton(ccp: CountryCodePicker) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                layoutViewModel.confirmButtonClicked.collect {
                    if (!isResumed) return@collect

                    val userName = binding.userNameText.text
                    val phone: String = binding.phoneInput.text.toString().filter { it.isDigit() }

                    var valid = true
                    if (phone.isEmpty()) {
                        binding.textInputLayoutPhone.error = null
                    }
                    else if (!ccp.isValidFullNumber) {
                        binding.textInputLayoutPhone.error =
                            getString(R.string.phone_invalid_message)
                        valid = false
                    } else {
                        binding.textInputLayoutPhone.error = null
                    }

                    if (userName.toString().isEmpty()) {
                        binding.userTextLayout.error = getString(R.string.user_name_empty)
                        valid = false
                    } else {
                        binding.userTextLayout.error = null
                    }

                    if (valid) {
                        userDataViewModel.setPhone(phone)
                        userDataViewModel.setName(userName.toString())

                        Log.i("ClientEditDataFragment", "phone: $phone, userName: $userName")

                        if (imageUri != null) {
                            imageFile = imageUri!!.uriToFile(requireContext())!!
                        }

                        uploadUserData(
                            imageFile,
                            PatchUserRequest(userName.toString(), phone)
                        )
                    }
                }
            }
        }
    }

}