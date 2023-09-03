package com.example.travelapp.ui

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.travelapp.R
import com.example.travelapp.data.models.LocationItem

class LocationDetailActivity : AppCompatActivity() {
    lateinit var enterPlanbtn: TextView
    lateinit var attractionName: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_detail)


        // Get the location item from the intent
        // Because the location item is a custom object, we need to use the getParcelableExtra method
        // to get the location item from the intent
        // getParcelableExtra is deprecated in API 30, so we need to use the if statement to check
        // the API version
        val item = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("location", LocationItem::class.java)
        } else {
            intent.getParcelableExtra<LocationItem>("location")
        }

        // Set up action bar
        enterPlanbtn = findViewById(R.id.enter_plan_button)
        attractionName = findViewById(R.id.attraction_name)
        attractionName.setText(item?.title)
        enterPlanbtn.setOnClickListener(){
            var intent: Intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}