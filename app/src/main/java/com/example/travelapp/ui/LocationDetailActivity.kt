package com.example.travelapp.ui

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.travelapp.R
import com.example.travelapp.data.models.LocationItem
import com.google.android.material.appbar.AppBarLayout

class LocationDetailActivity : AppCompatActivity() {
    lateinit var enterPlanbtn: TextView
    lateinit var attractionName: TextView
    lateinit var addToPlanBtn: TextView
    private lateinit var mActionBarToolbar: Toolbar
    private lateinit var titleToolBar: TextView

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

        addToPlanBtn = findViewById(R.id.add_to_plan_button)
        addToPlanBtn.setOnClickListener(){
            var intent: Intent = Intent(this, NewPlanActivity::class.java)
            intent.putExtra("location", item)
            startActivity(intent)
        }

        // Set up action bar
        enterPlanbtn = findViewById(R.id.enter_plan_button)
        attractionName = findViewById(R.id.attraction_name)
        attractionName.setText(item?.title)
        enterPlanbtn.setOnClickListener(){
            var intent: Intent = Intent(this, MapActivity::class.java)
            intent.putExtra("location", item)
            startActivity(intent)
        }
        setupToolBar()

    }

    private fun setupToolBar(){
        mActionBarToolbar = findViewById(R.id.toolbar_layout_detail_activity)
        setSupportActionBar(mActionBarToolbar)
        supportActionBar?.show()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}