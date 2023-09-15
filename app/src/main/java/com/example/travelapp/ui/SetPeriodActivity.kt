package com.example.travelapp.ui



import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import com.example.travelapp.R
import com.example.travelapp.data.models.LocationItem
import com.example.travelapp.data.models.ScheduleItem
import com.example.travelapp.ui.fragments.DatePickerFragment
import com.example.travelapp.ui.util.RandomString
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.util.Calendar
import java.util.Date


class SetPeriodActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    private var textView: TextView? = null
    var currentDayString: String? = null
    var dateTextView: Int = 0
    var scheduleItem: ScheduleItem = ScheduleItem()
    var locationItem: LocationItem? = null
    var currentTime: Date? = null
    var isEndDate: Boolean = false
    var nextButton: Button? = null
    private lateinit var mActionBarToolbar: Toolbar
    private lateinit var titleToolBar: TextView
    private lateinit var titleLocationTextView: TextView
    private lateinit var bannerImg: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_period)

        locationItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("location", LocationItem::class.java)
        } else {
            intent.getParcelableExtra<LocationItem>("location")
        }

        setupToolBar()

        scheduleItem.name = locationItem!!.title
        scheduleItem.id = RandomString.randomString(20)

        titleLocationTextView = findViewById(R.id.title_location)
        titleLocationTextView.text = locationItem!!.title

        bannerImg = findViewById(R.id.banner_image)

//        var idImage = this.resources.getIdentifier(locationItem?.image+"_banner",
//            "drawable", this.packageName) // R.drawable.image_name
//
//        bannerImg.setImageResource(idImage)

        CoroutineScope(Dispatchers.IO).launch {
            BitmapFactory.decodeFile(locationItem!!.imagePath)
            withContext(Dispatchers.Main) {
                bannerImg.setImageBitmap(BitmapFactory.decodeFile(locationItem!!.imagePath))
            }
        }

        var startTextView: TextView = findViewById(R.id.date_start)
        startTextView.setOnClickListener {
            isEndDate = false
            dateTextView = R.id.date_start
            val datePicker: DialogFragment = DatePickerFragment()
            datePicker.show(supportFragmentManager, "date picker")
            Log.d(TAG, "Time picker: $currentTime")
        }

        var endTextView: TextView = findViewById(R.id.date_end)
        endTextView.setOnClickListener {
            dateTextView = R.id.date_end
            isEndDate = true

            val datePicker: DialogFragment = DatePickerFragment()
            datePicker.show(supportFragmentManager, "date picker")
        }

        nextButton = findViewById(R.id.next_button)
        nextButton?.isEnabled = false
        nextButton?.setOnClickListener {
            val intent = Intent(this, TravelArrangementActivity::class.java)
            intent.putExtra("schedule", scheduleItem)
            intent.putExtra("location", locationItem)
            startActivity(intent)
        }

    }

    private fun setupToolBar(){
        mActionBarToolbar = findViewById(R.id.toolbar_layout)
        setSupportActionBar(mActionBarToolbar)
        supportActionBar?.show()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        titleToolBar = findViewById(R.id.toolbar_title)
        titleToolBar.text = "New Plan"

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        var c = Calendar.getInstance()
        c.set(Calendar.YEAR, p1)
        c.set(Calendar.MONTH, p2)
        c.set(Calendar.DAY_OF_MONTH, p3)
        currentTime = c.time

        if (!isEndDate){
            scheduleItem.startDate = Timestamp(Date(p1-1900,p2,p3))
            Log.d(TAG, "onDateSet: ${scheduleItem.startDate.toDate()}")

        }
        else{
            scheduleItem.endDate = Timestamp(Date(p1-1900, p2, p3))
            nextButton?.isEnabled = scheduleItem.endDate.toDate().time
                .compareTo(scheduleItem.startDate.toDate().time) >= 0
            Log.d(TAG, "onDateSet: ${scheduleItem.endDate.toDate()}")
        }



        // get attribute start date from locationItem with string stateDate, not get directly


        currentDayString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime())

        textView = findViewById(dateTextView)
        textView?.setText(currentDayString)


    }
}