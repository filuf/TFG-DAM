package com.raj.slotify

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.raj.slotify.databinding.ActivityMainBinding
import com.raj.slotify.viewModels.frontend.MainViewModel

class MainActivity : AppCompatActivity() {

    companion object {
        const val SPRING_ENDPOINT: String = "http://10.0.2.2:8080"
    }

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        AlertDialog.Builder(
            this,
            android.R.style.Theme_Material_Dialog_Alert
        ).setTitle("Hola, estas en main").setPositiveButton("OK", null).show()

    }
}