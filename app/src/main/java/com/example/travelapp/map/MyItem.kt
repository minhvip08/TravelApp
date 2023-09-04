package com.example.travelapp.map

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem



class MyItem(
    lat: Double,
    lng: Double,
    title: String,
    snippet: String,
    star: Double
) : ClusterItem {

    private val position: LatLng
    private val title: String
    private val snippet: String

    private val star: Double

    override fun getPosition(): LatLng {
        return position
    }

    override fun getTitle(): String {
        return title
    }

    override fun getSnippet(): String {
        return snippet
    }


    fun getZIndex(): Float {
        return 0f
    }
    fun getStar():Double {
        return star
    }

    init {
        position = LatLng(lat, lng)
        this.title = title
        this.snippet = snippet
        this.star = star
    }
}

