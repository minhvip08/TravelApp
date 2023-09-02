package com.example.travelapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.travelapp.R

class AttractionDetailActivity : AppCompatActivity() {
    lateinit var enterPlanbtn: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attraction_detail)

        // Set up action bar
        enterPlanbtn = findViewById(R.id.enter_plan_button)
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