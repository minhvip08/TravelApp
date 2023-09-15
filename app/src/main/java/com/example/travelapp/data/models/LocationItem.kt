package com.example.travelapp.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class LocationItem (
    var id: String,
    val title: String,
    val image: String,
    val description: String,
    val countryCode: String,
    var imagePath: String,
    var attraction: List<Attraction>) : Parcelable{
    constructor() : this("", "", "", "", "","", arrayListOf<Attraction>())
}
