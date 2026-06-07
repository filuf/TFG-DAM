package com.raj.slotify.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.raj.slotify.databinding.ActivityRegistryBinding
import com.raj.slotify.viewModels.room.TokenViewModel

class RegistryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistryBinding
    private val tokenViewModel: TokenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegistryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val paddingButton = if (imeInsets.bottom > 0) imeInsets.bottom else systemBars.bottom

            v.setPadding(systemBars.left, systemBars.top, systemBars.right, paddingButton)
            insets
        }

        val window = this.window
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        //GET USER TOKEN
        tokenViewModel.getLastToken()

    }
}