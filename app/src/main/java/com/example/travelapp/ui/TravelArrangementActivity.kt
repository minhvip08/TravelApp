package com.example.travelapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.travelapp.R
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

private const val DEFAULT_COUNTRY_CODE = "VN"

class TravelArrangementActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geoCoder: Geocoder
    private var scheduleItem: ScheduleItem?= null
    private var locationItem: LocationItem? = null
    private lateinit var fromCountryCode: TextView
    private lateinit var toCountryCode: TextView
    private lateinit var fromCountryIcon: TextView
    private lateinit var toCountryIcon: TextView
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
}