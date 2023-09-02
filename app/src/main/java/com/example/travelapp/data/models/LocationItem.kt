package com.example.travelapp.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class LocationItem (var id: String, val title: String, val image: String) {
    constructor() : this("", "", "")
}
