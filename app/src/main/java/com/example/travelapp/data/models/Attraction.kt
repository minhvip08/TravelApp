package com.example.travelapp.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Attraction (var id: String, var title: String, var latitude: Double, var longitude: Double) : Parcelable