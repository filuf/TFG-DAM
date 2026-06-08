package com.raj.slotify.tools

import android.util.Log
import androidx.navigation.NavController

object Navigation {

    fun navigateSafely(navController: NavController, destinationId: Int) {
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

}