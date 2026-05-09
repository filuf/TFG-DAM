package com.example.slotify.fragments.editUserData.client

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
import com.example.slotify.R
import com.example.slotify.databinding.FragmentClientEditDataBinding
import com.example.slotify.viewModels.LayoutViewModel
import com.example.slotify.viewModels.MainViewModel
import com.example.slotify.viewModels.UserDataViewModel
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

        viewLifecycleOwner.lifecycleScope.launch {
            layoutViewModel.nextButtonClicked.collect {

                val userName: String = binding.userNameText.text.toString()
                viewModelData.setName(userName)

                // TODO: HACER ALGO CON EL USERNAME

                imageUri?.let { viewModelData.setImageUri(it) }
            }
        }
    }

}