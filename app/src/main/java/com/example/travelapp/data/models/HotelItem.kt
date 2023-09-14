package com.example.travelapp.data.models

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HotelItem(
    var id: String,
    var title: String,
    var description: String,
    var price: Double,
    var rating: Double,
    var latitude: Double,
    var longitude: Double,
    var imageId: String,
    var imagePath: String,
    var locationIds: List<String>
) : Parcelable {
    constructor() : this("", "", "", 0.0, 0.0, 0.0, 0.0, "", "", arrayListOf<String>())
}