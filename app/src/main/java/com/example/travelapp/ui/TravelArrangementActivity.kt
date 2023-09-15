package com.example.travelapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.travelapp.R
import com.example.travelapp.data.models.HotelItem
import com.example.travelapp.data.models.LocationItem
import com.example.travelapp.data.models.ScheduleItem
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

private const val DEFAULT_COUNTRY_CODE = "VN"

class TravelArrangementActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geoCoder: Geocoder
    private var scheduleItem: ScheduleItem?= null
    private var locationItem: LocationItem? = null
    private var hotelItem: HotelItem? = null
    private lateinit var fromCountryCode: TextView
    private lateinit var toCountryCode: TextView
    private lateinit var fromCountryIcon: TextView
    private lateinit var toCountryIcon: TextView
    private lateinit var travelLayout: RelativeLayout
    private lateinit var hotelLayout: RelativeLayout
    private lateinit var hotelName: TextView
    private lateinit var hotelRatingBar: RatingBar
    private lateinit var hotelPrice: TextView
    private var numDay = 0

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (!permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)) {
            fromCountryCode.text = DEFAULT_COUNTRY_CODE
            fromCountryIcon.text = getFlagEmoji(DEFAULT_COUNTRY_CODE)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel_arrangement)

        // Set click listener for travel arrangement layout
        travelLayout = findViewById(R.id.travel_arrangement_layout_hotel_card)
        travelLayout.isEnabled = true

        // Permission check
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
        // Get data from intent
        getItemFromIntent()
        // Set up geocoder
        geoCoder = Geocoder(this, Locale.getDefault())
        // Get views
        fromCountryCode = findViewById(R.id.from_country_code)
        toCountryCode = findViewById(R.id.to_country_code)
        fromCountryIcon = findViewById(R.id.from_country_icon)
        toCountryIcon = findViewById(R.id.to_country_icon)
        // Get current location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_LOW_POWER, object: CancellationToken() {
            override fun isCancellationRequested(): Boolean {
                return false
            }

            override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                return CancellationTokenSource().token
            }
        }).addOnSuccessListener {location ->
            getCountryCode(location.latitude, location.longitude) {code ->
                fromCountryCode.text = code
                fromCountryIcon.text = getFlagEmoji(code)
                val priceAirline = findViewById<TextView>(R.id.travel_arrangement_airline_price)
                priceAirline.text = "From \$ ${setPriceAirline(location).toInt()}"
                setTotalPrice(location)
            }
        }


        // Set up toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar_travel_arrangement)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        // Set up determine the plan button
        val determineThePlanButton = findViewById<Button>(R.id.determine_the_plan_button)
        determineThePlanButton.setOnClickListener {
            val intent = Intent(this, ItineraryArrangementActivity::class.java)
            intent.putExtra("schedule", scheduleItem)
            intent.putExtra("location", locationItem)
            startActivity(intent)
        }
        // Set destination country
        toCountryCode.text = locationItem!!.countryCode
        toCountryIcon.text = getFlagEmoji(locationItem!!.countryCode)

        if (hotelItem != null){
            addHotelToSchedule()
        }

        travelLayout.setOnClickListener {
            val intent = Intent(this, ChooseHotelActivity::class.java)
            intent.putExtra("schedule", scheduleItem)
            intent.putExtra("location", locationItem)
            startActivity(intent)
        }



    }

    private fun getFlagEmoji(countryCode: String): String {
        val firstLetter = Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6
        val secondLetter = Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6
        return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
    }

    private fun getCountryCode(latitude: Double, longitude: Double, updateUi: (String) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geoCoder.getFromLocation(latitude, longitude, 1) {
                // Ensure main thread execution
                lifecycleScope.launch(Dispatchers.Main) {
                    updateUi(it[0].countryCode)
                }
            }
        } else {
            lifecycleScope.launch(Dispatchers.IO) {
                val result = geoCoder.getFromLocation(latitude, longitude, 1)
                if (result == null) {
                    // If this happens, something is wrong with the device
                    // Will not fix
                    throw Exception("Cannot get country name")
                } else {
                    withContext(Dispatchers.Main) {
                        updateUi(result[0].countryName)
                    }
                }
            }
        }
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

        // Calculate number of day
        calNumDay()

        hotelItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("hotel", HotelItem::class.java)
        } else {
            intent.getParcelableExtra("hotel")
        }

        if (hotelItem != null){

        }
    }

    private fun addHotelToSchedule(){
        val bitmap = BitmapFactory.decodeFile(hotelItem!!.imagePath)
        val ob = BitmapDrawable(resources, bitmap)
        ob.gravity = android.view.Gravity.CENTER


        hotelLayout = findViewById(R.id.travel_arrangement_layout_hotel_card)

        hotelLayout.background = ob

        hotelName = findViewById(R.id.travel_arrangement_hotel_name)
        hotelName.text = hotelItem!!.title
        hotelRatingBar = findViewById(R.id.travel_arrangement_hotel_rating)
        hotelRatingBar.rating = hotelItem!!.rating.toFloat()

        hotelPrice = findViewById(R.id.travel_arrangement_hotel_price)
        hotelPrice.text = "From \$ ${hotelItem!!.price.toString()}"
        travelLayout.isEnabled = false

    }

    fun setPriceAirline(locationTo : Location): Double{

        val locationFrom = Location("locationFrom")
        locationFrom.latitude = locationItem!!.attraction[0].latitude
        locationFrom.longitude = locationItem!!.attraction[0].longitude
        val distance = locationFrom.distanceTo(locationTo)/1000
        var price = distance * 0.17

        if (distance > 1000){
            price *= 0.8
        }

        return price



    }

    fun calNumDay(){
        val millionSeconds = scheduleItem!!.endDate.toDate().time - scheduleItem!!.startDate.toDate().time
        numDay = (millionSeconds / (1000 * 60 * 60 * 24)).toInt() + 1
    }

    fun setTotalPrice(locationTo : Location){
        val totalPriceTextView = findViewById<TextView>(R.id.travel_arrangement_total_price)
        val priceAirLine = setPriceAirline(locationTo)*2
        val totalPrice: Double = if (hotelItem != null){
            val priceHotel = hotelItem!!.price * numDay
            priceAirLine + priceHotel
        } else {
            priceAirLine
        }

        totalPriceTextView.text = "From \$ ${totalPrice.toInt()}"
    }

}