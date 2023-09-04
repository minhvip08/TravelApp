package com.example.travelapp.ui

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager


class MapActivity : AppCompatActivity() , OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private var locationItem: LocationItem? = null
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
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

//            setMarkerClickPopUp(cluster)

            true
        }



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
                MyItem(item.latitude, item.longitude, item.title, item.description, item.rating)
            clusterManager.addItem(offsetItem)
        }



    }

    private fun setMarkerClickPopUp(cluster: Cluster<MyItem>){
        titleTextView = findViewById(R.id.popular_location_name)
        descriptionTextView = findViewById(R.id.popular_location_description)
        titleTextView.setText(cluster.items.iterator().next().title)
        descriptionTextView.setText(cluster.items.iterator().next().snippet)

    }


}