package com.example.travelapp.ui

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.travelapp.R
import com.example.travelapp.data.models.LocationItem
import com.example.travelapp.data.models.ScheduleItem
import com.example.travelapp.ui.adapters.ViewPagerItineraryArrangementAdapter
import com.example.travelapp.ui.fragments.DayFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.Date

class ItineraryArrangementActivity : AppCompatActivity() {
    var scheduleItem: ScheduleItem? = null
    var locationItem: LocationItem? = null
    var numDay: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itinerary_arrangement)
        getItemFromPreviousActivity()

        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        val viewPager2: ViewPager2 = findViewById(R.id.view_pager_2)

        var endDate: Date = scheduleItem!!.endDate.toDate()
        val millionSeconds = scheduleItem!!.endDate.toDate().time - scheduleItem!!.startDate.toDate().time
        numDay = (millionSeconds / (1000 * 60 * 60 * 24)).toInt() + 1

        val adapter = ViewPagerItineraryArrangementAdapter(supportFragmentManager, lifecycle)

        for (i in 1..numDay){
            val fragment = DayFragment()
            val bundle = Bundle()
            bundle.putParcelable("location", locationItem)
            bundle.putParcelable("schedule", scheduleItem)
            bundle.putInt("day", i)
            fragment.arguments = bundle
            adapter.addFragment(fragment)
        }

        viewPager2.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = "Day ${position + 1}"
        }.attach()



    }

    fun getItemFromPreviousActivity(){
        locationItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("location", LocationItem::class.java)
        } else {
            intent.getParcelableExtra<LocationItem>("location")
        }
        scheduleItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("schedule", ScheduleItem::class.java)
        } else {
            intent.getParcelableExtra<ScheduleItem>("schedule")
        }
    }
}