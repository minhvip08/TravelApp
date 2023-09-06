package com.example.travelapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.travelapp.R
import com.example.travelapp.data.models.LocationItem
import com.example.travelapp.data.models.ScheduleItem

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ViewPagerItineraryArrangementAdapter(
    private var fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    val context: Context,
    val scheduleItem: ScheduleItem?) : FragmentStateAdapter(fragmentManager, lifecycle) {
    private val fragmentList: ArrayList<Fragment> = ArrayList()

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    fun addFragment(fragment: Fragment) {
        fragmentList.add(fragment)
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
    fun getTabView(i: Int) : LinearLayout {
        val dayTabItem = LayoutInflater.from(context).inflate(R.layout.day_tab_item, null) as LinearLayout
        dayTabItem.findViewById<TextView>(R.id.tab_item_title).text =
            context.getString(R.string.day_tab_item_title, i + 1)
        dayTabItem.findViewById<TextView>(R.id.tab_item_date).text =
            SimpleDateFormat("MMMM dd", Locale.getDefault()).format(
                Date(scheduleItem!!.startDate.toDate().time
                        + (i) * 24 * 60 * 60 * 1000)
            )
        return dayTabItem
    }
}