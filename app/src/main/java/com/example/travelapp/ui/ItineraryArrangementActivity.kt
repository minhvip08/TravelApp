package com.example.travelapp.ui

import android.content.ContentValues.TAG
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.viewpager2.widget.ViewPager2
import com.example.travelapp.R
import com.example.travelapp.data.ActivityItemViewModel
import com.example.travelapp.data.ItineraryViewModel
import com.example.travelapp.data.ScheduleViewModel
import com.example.travelapp.data.models.LocationItem
import com.example.travelapp.data.models.ScheduleItem
import com.example.travelapp.data.repository.ActivityItemRepository
import com.example.travelapp.data.repository.ItineraryRepository
import com.example.travelapp.data.repository.ScheduleRepository
import com.example.travelapp.ui.adapters.ViewPagerItineraryArrangementAdapter
import com.example.travelapp.ui.fragments.DayFragment
import com.example.travelapp.ui.util.FirestoreCollection
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Date

class ItineraryArrangementActivity : AppCompatActivity() {
    var scheduleItem: ScheduleItem? = null
    var locationItem: LocationItem? = null
    var dayFragmentList = ArrayList<DayFragment>()
    var numDay: Int = 0
    val user = Firebase.auth.currentUser
    val db = Firebase.firestore
    lateinit var btnNext: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itinerary_arrangement)
        getItemFromPreviousActivity()


        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        val viewPager2: ViewPager2 = findViewById(R.id.view_pager_2)

        var endDate: Date = scheduleItem!!.endDate.toDate()
        val millionSeconds = scheduleItem!!.endDate.toDate().time - scheduleItem!!.startDate.toDate().time
        numDay = (millionSeconds / (1000 * 60 * 60 * 24)).toInt() + 1
        Log.d(TAG, "onCreate: ${scheduleItem!!.startDate.toDate()}")
        val adapter = ViewPagerItineraryArrangementAdapter(supportFragmentManager, lifecycle, this, scheduleItem)


        for (i in 1..numDay){
            val fragment = DayFragment()
            dayFragmentList.add(fragment)
            val bundle = Bundle()
            bundle.putParcelable("location", locationItem)
            bundle.putParcelable("schedule", scheduleItem)
            bundle.putInt("day", i)
            fragment.arguments = bundle
            adapter.addFragment(fragment)
        }

        viewPager2.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.customView = adapter.getTabView(position)
        }.attach()

        btnNext = findViewById(R.id.next_button)
        btnNext.setOnClickListener {
            setScheduleToDatabase()
            for (i in 0 until numDay){
                setItineraryToDatabase(i)
                setActivityToDatabase(i)
            }
        }

    }

    private fun setScheduleToDatabase(){
        //add schedule to firebase

        val scheduleViewModel: ScheduleViewModel = ScheduleViewModel(ScheduleRepository(db))
        scheduleItem?.let { scheduleViewModel.setSchedule(user!!.uid, scheduleItem!!) }
    }

    private fun setItineraryToDatabase(position: Int){
        //add itinerary to firebase
        val itineraryViewModel: ItineraryViewModel = ItineraryViewModel(ItineraryRepository(db))
        itineraryViewModel.setItinerary(user!!.uid, scheduleItem!!.id,
            dayFragmentList[position].itineraryItem
        )
    }

    private fun setActivityToDatabase(position: Int){
        //add activity to firebase
        val activityViewModel: ActivityItemViewModel = ActivityItemViewModel(ActivityItemRepository(db))
        for (activity in dayFragmentList[position].activityList){
            activityViewModel.setActivity(user!!.uid,
                scheduleItem!!.id,
                dayFragmentList[position].itineraryItem.id,
                activity)
        }
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