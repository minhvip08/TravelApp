package com.example.travelapp.map

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem



class MyItem(
    lat: Double,
    lng: Double,
    title: String,
    snippet: String,
    rating: Float


) : ClusterItem {

    private val position: LatLng
    private val title: String
    private val snippet: String
    private val rating: Float

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

    fun getRating(): Float{
        return rating
    }

    init {
        position = LatLng(lat, lng)
        this.title = title
        this.snippet = snippet
        this.rating = rating

    }
}

