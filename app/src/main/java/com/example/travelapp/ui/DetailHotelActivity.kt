package com.example.travelapp.ui

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.travelapp.R
import com.example.travelapp.data.models.HotelItem
import com.example.travelapp.data.models.LocationItem
import com.example.travelapp.data.models.ScheduleItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class DetailHotelActivity : AppCompatActivity(), OnMapReadyCallback {
    private var hotelItem : HotelItem? = null
    private lateinit var mActionBarToolbar: Toolbar
    private lateinit var titleToolBar: TextView
    private lateinit var hotelImageView: ImageView
    private lateinit var hotelNameTextView: TextView
    private lateinit var hotelRatingBar: RatingBar
    private lateinit var hotelPriceTextView: TextView
    private lateinit var hotelDescriptionTextView: TextView
    private lateinit var askQuestionButton: Button
    private lateinit var ratingTextView: TextView
    private lateinit var mMap: GoogleMap
    private lateinit var nextButton: Button
    private var scheduleItem: ScheduleItem?= null
    private var locationItem: LocationItem? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_hotel)

        setupToolBar()

        getItemFromIntent()





        hotelImageView = findViewById(R.id.image_view_hotel)
        val bitmap = BitmapFactory.decodeFile(hotelItem!!.imagePath)
        hotelImageView.setImageBitmap(bitmap)

        hotelNameTextView = findViewById(R.id.name_hotel)
        hotelNameTextView.text = hotelItem!!.title

        hotelRatingBar = findViewById(R.id.rating_bar_hotel)
        hotelRatingBar.rating = hotelItem!!.rating.toFloat()

        ratingTextView = findViewById(R.id.score_hotel)
        ratingTextView.text = hotelItem!!.rating.toString()

        hotelPriceTextView = findViewById(R.id.price_hotel)
        hotelPriceTextView.text = "From \$ ${hotelItem!!.price.toString()} /night"

        hotelDescriptionTextView = findViewById(R.id.description_hotel_content)
        hotelDescriptionTextView.text = hotelItem!!.description

        askQuestionButton = findViewById(R.id.ask_question_hotel_button)
        askQuestionButton.setOnClickListener {
            val intent = Intent(this, ChatAiActivity::class.java)
            startActivity(intent)
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        nextButton = findViewById(R.id.book_now_hotel_button)
        nextButton.setOnClickListener {
            val intent = Intent(this, TravelArrangementActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("hotel", hotelItem)
            intent.putExtra("location", locationItem)
            intent.putExtra("schedule", scheduleItem)
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupToolBar(){
        mActionBarToolbar = findViewById(R.id.toolbar_layout)
        setSupportActionBar(mActionBarToolbar)
        supportActionBar?.show()
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        titleToolBar = findViewById(R.id.toolbar_title)
        titleToolBar.text = "Choose Hotel"
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setUpCluster()
    }

    fun setUpCluster(){
        val latLng = LatLng(
            hotelItem!!.latitude,
            hotelItem!!.longitude
        )
        // Position the map.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
        mMap.addMarker(MarkerOptions().position(latLng).title(hotelItem!!.title))

    }

    private fun getItemFromIntent(){
        locationItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("location", LocationItem::class.java)
        } else {
            intent.getParcelableExtra("location")
        }

        scheduleItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("schedule", ScheduleItem::class.java)
        } else {
            intent.getParcelableExtra("schedule")
        }

        hotelItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("hotel", HotelItem::class.java)
        } else {
            intent.getParcelableExtra<HotelItem>("hotel")
        }
    }
}