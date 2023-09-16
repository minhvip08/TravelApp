package com.example.travelapp.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.travelapp.data.models.ScheduleItem
import com.example.travelapp.ui.fragments.ScheduleListFragment
import com.google.firebase.Timestamp

class ScheduleViewPagerAdapter(
    fragment: Fragment,
    scheduleList: List<ScheduleItem>
) : FragmentStateAdapter(fragment) {

    private val sortedLists = scheduleList.partition {
        it.endDate.seconds + 86400 > Timestamp.now().seconds
    }

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ScheduleListFragment.newInstance(false, sortedLists.first as ArrayList<ScheduleItem>)
            else -> ScheduleListFragment.newInstance(true, sortedLists.second as ArrayList<ScheduleItem>)
        }
    }
}