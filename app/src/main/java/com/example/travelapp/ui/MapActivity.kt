package com.example.travelapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.travelapp.R
import com.example.travelapp.map.MyItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager


class MapActivity : AppCompatActivity() , OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        setUpClusterer()
    }


    // Declare a variable for the cluster manager.
    private lateinit var clusterManager: ClusterManager<MyItem>

    private fun setUpClusterer() {
        // Position the map.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(51.503186, -0.126446), 10f))

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        clusterManager = ClusterManager(this, mMap)



        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(clusterManager)
        mMap.setOnMarkerClickListener { marker -> // Check if a click count was set, then display the click count.
            if (marker.isInfoWindowShown) {
                marker.hideInfoWindow()
            } else {
                marker.showInfoWindow()
            }
            true
        }

        // Add cluster items (markers) to the cluster manager.
        addItems()
    }

    private fun addItems() {

        // Set some lat/lng coordinates to start with.
        var lat = 51.5145160
        var lng = -0.1270060

        // Add ten cluster items in close proximity, for purposes of this example.
        for (i in 0..9) {
            val offset = i / 60.0
            lat += offset
            lng += offset
            val offsetItem =
                MyItem(lat, lng, "Title $i", "Snippet $i")
            clusterManager.addItem(offsetItem)
        }



// Set the title and snippet strings.
        val title = "This is the title"
        val snippet = "and this is the snippet."

// Create a cluster item for the marker and set the title and snippet using the constructor.
        val infoWindowItem = MyItem(lat, lng, title, snippet)

// Add the cluster item (marker) to the cluster manager.
        clusterManager.addItem(infoWindowItem)
    }


}