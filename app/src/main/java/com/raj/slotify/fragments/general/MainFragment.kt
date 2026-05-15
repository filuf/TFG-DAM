package com.raj.slotify.fragments.general

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.raj.slotify.R
import com.raj.slotify.databinding.FragmentMainBinding
import com.raj.slotify.viewModels.frontend.LayoutViewModel

class MainFragment : Fragment() {

    private val layoutViewModel: LayoutViewModel by activityViewModels()
    private val viewModel: ViewModel by activityViewModels()

    private lateinit var binding: FragmentMainBinding
    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val window = requireActivity().window
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        // 2. Hacer que la BottomNavigationView se extienda debajo de la barra del sistema
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavigationView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.updatePadding(bottom = systemBars.bottom)
            binding.toolbar.setPadding(0, systemBars.top, 0, 0)
            binding.navigationView.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        val activity = requireActivity() as androidx.appcompat.app.AppCompatActivity
        activity.setSupportActionBar(binding.toolbar)

        // BLOCK SYSTEM BACK BUTTON
        activity.onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finishAffinity()
            }
        })

        drawerToggle = ActionBarDrawerToggle(
            activity,
            binding.drawerLayout,
            binding.toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )

        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        // CHANGE ICON TO HAMBURGER
        val hamburgerIcon = drawerToggle.drawerArrowDrawable
        hamburgerIcon.progress = 0f // 0 is hamburger icon

        binding.toolbar.navigationIcon = hamburgerIcon

        binding.toolbar.setNavigationOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(androidx.core.view.GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
            } else {
                binding.drawerLayout.openDrawer(androidx.core.view.GravityCompat.START)
            }
        }

        // SET UP NAV HOST FRAGMENT
        val navHostFragment = childFragmentManager.findFragmentById(R.id.navHostFragmentHome) as NavHostFragment
        val navController = navHostFragment.navController

        binding.navigationView.setupWithNavController(navController)

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            Log.i("MainFragment", "Item selected: ${item.itemId}")
            when(item.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.calendarFragment -> {
                    navController.navigate(R.id.calendarFragment)
                    true
                }
                R.id.viewAllReservesFragment -> {
                    navController.navigate(R.id.viewAllReservesFragment)
                    true
                }
                R.id.reserveAService -> {
                    // TODO: ACTIVITY TO RESERVE
                    true
                }
                R.id.inqueriesChat -> {
                    //navController.navigate(R.id.chatForInqueriesFragment)
                    false
                }
                R.id.settingsFragment -> {
                    navController.navigate(R.id.settingsFragment)
                    true
                }
                else -> false
            }
        }

        val menuHost: androidx.core.view.MenuHost = requireActivity()
        menuHost.addMenuProvider(object : androidx.core.view.MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_toolbar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.settingsFragment -> {
                        navController.navigate(R.id.settingsFragment)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)

    }

}