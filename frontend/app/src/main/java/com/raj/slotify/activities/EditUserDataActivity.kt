package com.raj.slotify.activities

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.raj.slotify.R
import com.raj.slotify.databinding.ActivityEditUserDataBinding
import com.raj.slotify.viewModels.frontend.LayoutViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel
import com.raj.slotify.viewModels.room.TokenViewModel
import java.util.UUID

class EditUserDataActivity : AppCompatActivity() {

    private val userDataViewModel: UserDataViewModel by viewModels()
    private val layoutViewModel: LayoutViewModel by viewModels()
    private val tokenViewModel: TokenViewModel by viewModels()
    private lateinit var binding: ActivityEditUserDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEditUserDataBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val window = this.window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        tokenViewModel.getLastToken()

        listenId()
        setFragment()
        setConfirmButton()
        setCancelButton()

        setBackButton()
        setButtonsVisibility()
    }

    private fun listenId() {
        val id = intent.getStringExtra("ID")
        if (id == null) {
            Log.e("EditUserDataActivity", "No se ha recibido el ID")
            return
        }
        Log.i("EditUserDataActivity", "ID recibido: $id")
        userDataViewModel.setUuid(UUID.fromString(id))
    }

    fun setConfirmButton() {
        val confirmButton = binding.confirmChangesButton

        confirmButton.setOnClickListener {
            confirmButton.isEnabled = false
            layoutViewModel.onConfirmClicked()

            confirmButton.postDelayed({ confirmButton.isEnabled = true }, 2000)
        }
    }

    fun setBackButton() {
        val backButton = binding.editUserGoBackButton
        backButton.visibility = View.GONE

        backButton.setOnClickListener {
            layoutViewModel.onBackClicked()
        }
    }

    fun setCancelButton() {
        val cancelButton = binding.cancelChangesButton
        cancelButton.visibility = View.VISIBLE

        cancelButton.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.cancel_changes_title))
                .setMessage(getString(R.string.cancel_changes_question))
                .setPositiveButton(getString(R.string.yes_word)) { _, _ ->
                    finish()
                }.setNegativeButton(getString(R.string.no_word), null)
                .show()
        }
    }

    fun setButtonsVisibility() {
        layoutViewModel.cancelButtonVisibility.observe(this) { visibility ->
            binding.cancelChangesButton.visibility = visibility
        }
        layoutViewModel.backButtonVisibility.observe(this) { visibility ->
            binding.editUserGoBackButton.visibility = visibility
        }
    }

    fun setFragment() {
        layoutViewModel.setExplicationVisibility(View.GONE)
        layoutViewModel.setDescriptionVisibility(View.GONE)
        layoutViewModel.setImageVisibility(View.GONE)

        layoutViewModel.setBackButtonVisibility(View.GONE)
        layoutViewModel.setCancelButtonVisibility(View.VISIBLE)

        val userType = intent.getStringExtra("USER_TYPE")

        if (userType == null) {
            Toast.makeText(this, "No se ha recibido el tipo de usuario", Toast.LENGTH_SHORT).show()
            return
        }
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerViewEditUserData) as NavHostFragment
        val navController = navHostFragment.navController

        val navInflater = navController.navInflater
        val graph = navInflater.inflate(R.navigation.nav_graph_edit_data)

        when (userType) {
            "USER" -> {
                graph.setStartDestination(R.id.clientEditDataFragment)
            } else -> {
                graph.setStartDestination(R.id.companyEditDataFragment)
            }
        }

        navController.graph = graph
    }
}