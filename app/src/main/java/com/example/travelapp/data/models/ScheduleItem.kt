package com.example.travelapp.data.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScheduleItem(
    var name: String,
    var startDate: Timestamp,
    var endDate: Timestamp,
    var itineraries: MutableList<ItineraryItem>
) : Parcelable {
    constructor() : this("", Timestamp(0, 0), Timestamp(0, 0), mutableListOf())
}