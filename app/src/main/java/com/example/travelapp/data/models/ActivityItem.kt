package com.example.travelapp.data.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class ActivityItem(
    var detail: String,
    var name: String,
    var time: Timestamp
) : Parcelable {
    constructor() : this("", "", Timestamp(0, 0))
}