package com.example.travelapp.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.travelapp.ui.MainActivity
import com.example.travelapp.ui.fragments.ArticlesFragment
import com.example.travelapp.ui.fragments.GuideFragment
import com.example.travelapp.ui.fragments.HomeFragment
import com.example.travelapp.ui.fragments.ScheduleFragment
import com.example.travelapp.ui.fragments.ScheduleWrapperFragment

class MainViewPagerAdapter(
    activity: MainActivity
) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> ScheduleWrapperFragment()
//            2 -> GuideFragment()
            else -> ArticlesFragment()
        }
    }

}