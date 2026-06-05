package com.raj.slotify.adapters

import androidx.lifecycle.Lifecycle import androidx.viewpager2.adapter.FragmentStateAdapter
import com.raj.slotify.fragments.general.TabReservesContentFragment

class ReservesPagerAdapter( fragmentManager: androidx.fragment.app.FragmentManager, lifecycle: Lifecycle ) : FragmentStateAdapter(fragmentManager,lifecycle) {
    private val fragments = listOf(TabReservesContentFragment(), TabReservesContentFragment(), TabReservesContentFragment())
    override fun getItemCount() = fragments.size
    override fun createFragment(position: Int) = fragments[position]
}
