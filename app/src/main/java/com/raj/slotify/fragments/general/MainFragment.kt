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
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginEnd
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.raj.slotify.R
import com.raj.slotify.activities.LogOutActivity
import com.raj.slotify.databinding.FragmentMainBinding
import com.raj.slotify.viewModels.frontend.LayoutViewModel
import com.raj.slotify.viewModels.frontend.MainViewModel
import com.raj.slotify.viewModels.frontend.UserDataViewModel

class MainFragment : Fragment() {

    private val layoutViewModel: LayoutViewModel by activityViewModels()
    private val viewModel: MainViewModel by activityViewModels()
    private val userDataViewModel: UserDataViewModel by activityViewModels()

    private lateinit var binding: FragmentMainBinding
    private lateinit var drawerToggle: ActionBarDrawerToggle

    private lateinit var upNavFragment: NavHostFragment
    private lateinit var upNavController: NavController


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

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }

        // MANAGE THE UP FRAGMENT
        upNavFragment = childFragmentManager.findFragmentById(R.id.fragmentUp) as NavHostFragment
        upNavController = upNavFragment.navController

        layoutViewModel.superiorFragmentVisibility.observe(viewLifecycleOwner) { visibility ->
            binding.fragmentUp.visibility = visibility
        }

        layoutViewModel.downFragmentFullScreenSize.observe(viewLifecycleOwner) { zeroAppMargin ->
            val params = binding.mainLinearLayout.layoutParams as ViewGroup.MarginLayoutParams

            if (zeroAppMargin) {
                params.setMargins(0, 0, 0, 0)
            } else {
                val margin32dp = (32 * resources.displayMetrics.density).toInt()
                params.setMargins(margin32dp, margin32dp, margin32dp, 0)
            }

            binding.mainLinearLayout.layoutParams = params
        }

        layoutViewModel.centerIconTitle.observe(viewLifecycleOwner) { centerIconTitle ->
            val currentDestinationId = upNavController.currentDestination?.id

            if (centerIconTitle) {
                if (currentDestinationId == R.id.upFragment) {
                    upNavController.navigate(R.id.action_upFragment_to_centerTextNormalIconFragment)
                }
            } else {
                if (currentDestinationId == R.id.centerTextNormalIconFragment) {
                    upNavController.navigate(R.id.action_centerTextNormalIconFragment_to_upFragment)
                }
            }
        }

        // EXTEND BOTTOM NAVIGATION IN SYSTEM NAV BAR
        val window = requireActivity().window
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

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

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.closeSesion -> {
                    val closeSessionIntent = Intent(requireContext(), LogOutActivity::class.java)

                    val tokenEntity = userDataViewModel.userToken.value

                    if (tokenEntity != null) {

                        val idToken = tokenEntity.idToken
                        closeSessionIntent.putExtra("ID_TOKEN", idToken)

                        startActivity(closeSessionIntent)
                    } else {
                        Log.e("MainFragment", "No hay entidad de token disponible")
                    }
                    true
                }
                else -> true
            }
        }

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
                        upNavController.popBackStack()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)

    }

}