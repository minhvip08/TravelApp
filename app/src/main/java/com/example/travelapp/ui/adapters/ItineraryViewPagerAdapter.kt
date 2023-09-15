package com.example.travelapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.travelapp.R
import com.example.travelapp.data.models.ItineraryItem
import com.example.travelapp.ui.fragments.DayReadOnlyFragment
import java.text.SimpleDateFormat
import java.util.Locale

class ItineraryViewPagerAdapter(
    fragment: Fragment,
    private val context: Context,
    private val scheduleId: String,
    private val itineraryList: List<ItineraryItem>
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return itineraryList.size
    }

    override fun createFragment(position: Int): Fragment {
        return DayReadOnlyFragment.newInstance(scheduleId, itineraryList[position].id)
    }

    fun getTabView(i: Int) : LinearLayout {
        val dayTabItem = LayoutInflater.from(context).inflate(R.layout.day_tab_item, null) as LinearLayout
        dayTabItem.findViewById<TextView>(R.id.tab_item_title).text =
            context.getString(R.string.day_tab_item_title, i + 1)
        dayTabItem.findViewById<TextView>(R.id.tab_item_date).text =
            SimpleDateFormat("dd MMMM", Locale.getDefault()).format(
                itineraryList[i].date.toDate()
            )
        return dayTabItem
    }
}