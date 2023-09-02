package com.example.travelapp.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationItem (var id: String, val title: String, val image: Int) :
    Parcelable
