package com.example.travelapp.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.travelapp.data.models.ItineraryItem
import com.example.travelapp.ui.fragments.DayReadOnlyFragment

class ItineraryViewPagerAdapter(
    private val supportFragmentManager: FragmentManager,
    private val lifecycle: Lifecycle,
    val itineraryList: List<ItineraryItem>
) : FragmentStateAdapter(supportFragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun createFragment(position: Int): Fragment {
        return DayReadOnlyFragment.newInstance(itineraryList[position])
    }
}