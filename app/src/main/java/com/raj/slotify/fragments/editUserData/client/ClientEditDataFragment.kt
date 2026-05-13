package com.raj.slotify.fragments.editUserData.client

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.raj.slotify.R
import com.raj.slotify.databinding.FragmentClientEditDataBinding
import com.raj.slotify.viewModels.frontend.LayoutViewModel
import com.raj.slotify.viewModels.frontend.MainViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel
import kotlinx.coroutines.launch
import java.io.File

class ClientEditDataFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val viewModelData: UserDataViewModel by activityViewModels()
    private val layoutViewModel: LayoutViewModel by activityViewModels()

    private lateinit var binding: FragmentClientEditDataBinding

    private var latestTmpUri: Uri? = null
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layoutViewModel.setExplicationVisibility(View.GONE)
        viewModel.setNewTitle(R.string.enter_user_data)

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

        val cardImage: View = binding.cardImage

        cardImage.setOnClickListener {
            openImagePicker()
        }

        val ccp = binding.ccp
        val phoneInput = binding.phoneInput
        ccp.registerCarrierNumberEditText(phoneInput)

        viewLifecycleOwner.lifecycleScope.launch {
            layoutViewModel.nextButtonClicked.collect {

                val userName: String = binding.userNameText.text.toString()
                val phone: String = ccp.fullNumberWithPlus
                val isValid = ccp.isValidFullNumber
                val email: String = binding.emailInput.text.toString()

                if (email.isEmpty() ||
                    phone.isEmpty() ||
                    userName.isEmpty()) {

                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.email_phone_empty_title))
                        .setMessage(getString(R.string.email_phone_empty_camps))
                        .setPositiveButton(getString(R.string.dialog_retry), null)
                        .show()
                }
                else if (!isValid) {

                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.phone_invalid_title))
                        .setMessage(getString(R.string.phone_invalid_message))
                        .setPositiveButton(getString(R.string.dialog_retry), null)
                        .show()

                } else {
                    // TODO: CONECTAR A BASE DE DATOS Y VERIFICAR QUE EXISTA UN USUARIO CON ESE EMAIL O TELÉFONO

                    var phoneNumberExists = false
                    var emailExists = false

                    if (phoneNumberExists || emailExists) {
                        // TODO: REDIRIGIR AL USUARIO A LA PANTALLA DE INICIO
                    }

                    viewModelData.setPhone(phone)
                    viewModelData.setEmail(email)
                    viewModelData.setName(userName)

                    imageUri?.let { viewModelData.setImageUri(it) }

                    view.findNavController()
                        .navigate(R.id.action_clientRegistryPhoneEmailFragment2_to_passwordFragment)
                }
            }
        }
    }

}