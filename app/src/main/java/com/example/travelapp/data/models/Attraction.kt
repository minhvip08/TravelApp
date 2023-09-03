package com.example.travelapp.data.models

import android.os.ParcelFileDescriptor
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Attraction (
    var id: String,
    var title: String,
    var description: String,
    var rating: Double,
    var latitude: Double,
    var longitude: Double): Parcelable{
    constructor() : this("", "","",0.0, 0.0, 0.0)
}