package com.example.travelapp.data.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItineraryItem(
    var id: String,
    var date: Timestamp
) : Parcelable {
    constructor() : this("", Timestamp(0, 0))
}