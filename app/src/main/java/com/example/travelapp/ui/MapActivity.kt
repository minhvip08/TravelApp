package com.example.travelapp.ui

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.travelapp.R
import com.example.travelapp.data.models.LocationItem
import com.example.travelapp.map.MyItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager


class MapActivity : AppCompatActivity() , OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private var locationItem: LocationItem? = null
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var LocationTitleTextView: TextView
    private lateinit var floatingNextBtn: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        floatingNextBtn = findViewById(R.id.floating_next_button)
        floatingNextBtn.setOnClickListener {
            var intent: Intent = Intent(this, NewPlanActivity::class.java)
            startActivity(intent)

        }



    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        setUpCluster()
    }

    // Declare a variable for the cluster manager.
    private lateinit var clusterManager: ClusterManager<MyItem>


    private fun setUpCluster() {

        locationItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("location", LocationItem::class.java)
        } else {
            intent.getParcelableExtra<LocationItem>("location")
        }

        LocationTitleTextView = findViewById(R.id.title_location)
        LocationTitleTextView.setText("Location in " + locationItem!!.title)

        val latLng = LatLng(
            locationItem!!.attraction[0].latitude,
            locationItem!!.attraction[0].longitude
        )
        // Position the map.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        clusterManager = ClusterManager(this, mMap)

        clusterManager.setOnClusterClickListener { cluster ->
            // Show a toast with some info when the cluster is clicked.
            val firstName = cluster.items.iterator().next().title
            Toast.makeText(
                this,
                cluster.size.toString() + " (including " + firstName + ")",
                Toast.LENGTH_SHORT
            ).show()
            true
        }

        clusterManager.setOnClusterItemClickListener { item ->
            // Do something with the clicked item
            Toast.makeText(
                this,
                item.title,
                Toast.LENGTH_SHORT
            ).show()
            setMarkerClickPopUp(item)
            true }


        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(clusterManager)
        mMap.setOnMarkerClickListener(clusterManager)

        // Add cluster items (markers) to the cluster manager.
        addItems()



    }

    private fun addItems() {


        // Add ten cluster items in close proximity, for purposes of this example.
        for (item in locationItem!!.attraction) {
            val offsetItem =
                MyItem(item.latitude, item.longitude, item.title, item.description, item.rating.toFloat())
            clusterManager.addItem(offsetItem)
        }



    }

    private fun setMarkerClickPopUp(item: MyItem){
        titleTextView = findViewById(R.id.popular_location_name)
        descriptionTextView = findViewById(R.id.popular_location_description)
        ratingBar = findViewById(R.id.rating_bar_popup)
        titleTextView.setText(item.title)
        descriptionTextView.setText(item.snippet)
        ratingBar.setRating(item.getRating().toFloat())

    }


}