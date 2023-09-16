package com.example.travelapp.ui

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
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
    private lateinit var mActionBarToolbar: Toolbar
    private lateinit var titleToolBar: TextView

    val scheduleViewModel = ScheduleViewModel(ScheduleRepository(db))
    val itineraryViewModel = ItineraryViewModel(ItineraryRepository(db))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itinerary_arrangement)
        getItemFromPreviousActivity()


        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        val viewPager2: ViewPager2 = findViewById(R.id.view_pager_2)

        val endDate: Date = scheduleItem!!.endDate.toDate()
        val millionSeconds = endDate.time - scheduleItem!!.startDate.toDate().time
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
            var isAllDayFragmentAdded = checkAllDayFragment()
            if (!isAllDayFragmentAdded){
                Toast.makeText(this, "Please add activity to all day", Toast.LENGTH_SHORT).show()
            }
            else {
                setScheduleToDatabase()
                val intent = Intent(this, AddScheduleCompletelyActivity::class.java)
                startActivity(intent)
            }



        setupToolBar()

        }

    }

    private fun checkAllDayFragment(): Boolean{
        dayFragmentList.forEach { fragment ->
            if (!fragment.itineraryItem.isAddedActivity){
                return false
            }

        }
        return true
    }

    private fun setupToolBar(){
        mActionBarToolbar = findViewById(R.id.toolbar_layout)
        setSupportActionBar(mActionBarToolbar)
        supportActionBar?.show()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        titleToolBar = findViewById(R.id.toolbar_title)
        titleToolBar.text = "Add Itinerary"

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setScheduleToDatabase() {
        scheduleItem!!.image = locationItem!!.image
        scheduleViewModel.setSchedule(user!!.uid, scheduleItem!!) {
            setItineraryToDatabase()
        }
    }

    private fun setItineraryToDatabase() {
        for (i in 0 until numDay) {
            itineraryViewModel.setItinerary(
                user!!.uid,
                scheduleItem!!.id,
                dayFragmentList[i].itineraryItem,
            ) {
                setActivityToDatabase(i)
            }
        }

    }

    private fun setActivityToDatabase(position: Int) {
        //add activity to firebase
        val activityViewModel = ActivityItemViewModel(ActivityItemRepository(db))
        for (activity in dayFragmentList[position].activityList) {
            activityViewModel.setActivity(user!!.uid,
                scheduleItem!!.id,
                dayFragmentList[position].itineraryItem.id,
                activity)
        }
    }

    private fun getItemFromPreviousActivity(){
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