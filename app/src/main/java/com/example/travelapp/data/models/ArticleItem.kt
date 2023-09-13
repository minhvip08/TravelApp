package com.example.travelapp.data.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArticleItem(
    val pid: String,
    val time: Timestamp,
    val title: String,
    val uid: String,
    val url: String
) : Parcelable {
    constructor() : this("",Timestamp(0, 0), "", "", "")
}