package com.raj.slotify.fragments.general

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.raj.slotify.R
import com.raj.slotify.activities.LogOutActivity
import com.raj.slotify.activities.client.MakeReserveActivity
import com.raj.slotify.activities.company.AddEditServiceActivity
import com.raj.slotify.databinding.FragmentMainBinding
import com.raj.slotify.dtos.company.GetServicesResponse
import com.raj.slotify.fragments.company.services.ServicesFragment.Companion.EXTRA_AUTH_TOKEN
import com.raj.slotify.fragments.company.services.ServicesFragment.Companion.EXTRA_COMPANY_ID
import com.raj.slotify.fragments.company.services.ServicesFragment.Companion.EXTRA_SERVICE_ID
import com.raj.slotify.fragments.company.services.ServicesFragment.Companion.EXTRA_SERVICE_NAME
import com.raj.slotify.fragments.company.services.ServicesFragment.Companion.REQUEST_ADD_SERVICE
import com.raj.slotify.viewModels.frontend.LayoutViewModel
import com.raj.slotify.viewModels.frontend.MainViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel

class MainFragment : Fragment() {

    private val layoutViewModel: LayoutViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val userDataViewModel: UserDataViewModel by activityViewModels()

    private lateinit var binding: FragmentMainBinding
    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layoutViewModel.setDescriptionVisibility(View.GONE)
        layoutViewModel.setExplicationVisibility(View.GONE)

        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment = childFragmentManager.findFragmentById(R.id.navHostFragmentHome) as NavHostFragment
        val navController = navHostFragment.navController

        val activity = requireActivity() as AppCompatActivity

        setUpToolbar(activity)

        observeUserType(navController)

        configViewPaddings()

        setUpNavController(navController)

        overrideBackButtonBehaviour(activity, navController)

        setNavViewHeader()
    }

    private fun setNavViewHeader() {
        val headerView = binding.navigationView.getHeaderView(0)

        if (headerView == null) {
            Log.e("MainFragment", "No se encontró el Header del NavigationView")
            return
        }

        val userNameHeader = headerView.findViewById<TextView>(R.id.tv_user_name_header)
        val userImageHeader = headerView.findViewById<ImageView>(R.id.iv_user_profile)

        userDataViewModel.name.observe(viewLifecycleOwner) { name ->
            if (name != null) {
                userNameHeader?.text = name
            }
        }

        userDataViewModel.imageUri.observe(viewLifecycleOwner) { imageUri ->
            if (imageUri != null) {
                userImageHeader?.setImageURI(imageUri)
            }
        }
    }

    private fun overrideBackButtonBehaviour(
        activity: AppCompatActivity,
        navController: NavController
    ) {
        activity.onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // IF LATERAL MENU IS OPEN CLOSE IT
                    if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        binding.drawerLayout.closeDrawer(GravityCompat.START)
                    }
                    // IF NAV CONTROLLER CAN GO BACK
                    else if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    }
                    // IF NAV CONTROLLER CANNOT GO BACK
                    else {
                        requireActivity().finishAffinity()
                    }
                }
            })
    }

    private fun setUpNavController(navController: NavController) {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_toolbar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.settingsFragment -> {
                        navigateSafely(navController, R.id.settingsFragment)
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner)

        // SET UP MENUS
        val hamburgerIcon = drawerToggle.drawerArrowDrawable
        hamburgerIcon.progress = 0f // 0 is hamburger icon
        binding.toolbar.navigationIcon = hamburgerIcon

        layoutViewModel.bottomNavVisibility.observe(viewLifecycleOwner) { visibility ->
            binding.bottomNavigationView.visibility = visibility
        }
    }

    private fun configViewPaddings() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)

            binding.toolbar.setPadding(0, systemBars.top, 0, 0)
            binding.navigationView.setPadding(0, systemBars.top, 0, 0)

            binding.bottomNavigationView.updatePadding(bottom = systemBars.bottom)
            insets
        }
        val window = requireActivity().window
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
    }

    private fun observeUserType(navController: NavController) {
        userDataViewModel.userType.observe(viewLifecycleOwner) { userType ->
            Log.i("userType", userType)

            binding.bottomNavigationView.menu.clear()
            binding.navigationView.menu.clear()

            if (userType == "USER") {
                binding.bottomNavigationView.inflateMenu(R.menu.bottom_navigation_menu_client)
                binding.navigationView.inflateMenu(R.menu.nav_menu_client)
            } else {
                binding.bottomNavigationView.inflateMenu(R.menu.bottom_navigation_menu_company)
                binding.navigationView.inflateMenu(R.menu.nav_menu_company)
            }

            binding.bottomNavigationView.setupWithNavController(navController)
            binding.navigationView.setupWithNavController(navController)

            setupDrawerClickListeners(navController)

            drawerToggle.syncState()
        }
    }

    private fun setUpToolbar(activity: AppCompatActivity) {
        activity.setSupportActionBar(binding.toolbar)

        // SET DRAWER TOGGLE
        drawerToggle = ActionBarDrawerToggle(
            activity,
            binding.drawerLayout,
            binding.toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
    }

    fun syncToolbar() {
        drawerToggle.syncState()
    }

    private fun navigateSafely(navController: NavController, destinationId: Int) {
        val currentDestinationId = navController.currentDestination?.id

        if (currentDestinationId == destinationId) return

        val navOptions = androidx.navigation.NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setRestoreState(true)
            .setPopUpTo(navController.graph.startDestinationId, inclusive = false, saveState = true)
            .build()

        try {
            navController.navigate(destinationId, null, navOptions)
        } catch (e: Exception) {
            Log.e("NavError", "No se pudo navegar al destino: $destinationId")
        }
    }

    private fun openAddEditService(service: GetServicesResponse?) {
        val companyId = userDataViewModel.uuid.value ?: return
        val token = userDataViewModel.userToken.value?.accessToken ?: return

        val intent = Intent(requireActivity(), AddEditServiceActivity::class.java).apply {
            putExtra(EXTRA_COMPANY_ID, companyId.toString())
            putExtra(EXTRA_AUTH_TOKEN, token)
            if (service != null) {
                putExtra(EXTRA_SERVICE_ID, service.serviceId.toString())
                putExtra(EXTRA_SERVICE_NAME, service.serviceName)
            }
        }
        startActivityForResult(intent, REQUEST_ADD_SERVICE)
    }

    private fun setupDrawerClickListeners(navController: NavController) {
        binding.toolbar.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(androidx.core.view.GravityCompat.START)
        }
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.closeSesion -> {
                    val intent = Intent(requireActivity(), LogOutActivity::class.java)
                    val tokenEntity = userDataViewModel.userToken.value
                    if (tokenEntity != null) {
                        intent.putExtra("ID_TOKEN", tokenEntity.idToken)
                    }
                    startActivity(intent)
                    binding.drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
                    true
                }

                R.id.reserveAService -> {
                    val intent = Intent(requireActivity(), MakeReserveActivity::class.java)
                    startActivity(intent)
                    binding.drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
                    true
                }

                R.id.addService -> {
                    openAddEditService(null)
                    binding.drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
                    true
                }

                R.id.manageExceptions -> {
                    // TODO: NAVEGAR A LA PÁGINA DE INTERVALOS

                    binding.drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
                    true
                }

                else -> {
                    val handled = androidx.navigation.ui.NavigationUI.onNavDestinationSelected(menuItem, navController)
                    if (handled) {
                        binding.drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
                    }
                    handled
                }
            }
        }
    }

}