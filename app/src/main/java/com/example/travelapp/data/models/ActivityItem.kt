package com.example.travelapp.data.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class ActivityItem(
    val id: String,
    val name: String,
    val time: Timestamp
) : Parcelable {
    constructor() : this("","", Timestamp(0, 0))
}