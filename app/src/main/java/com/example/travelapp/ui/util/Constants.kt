package com.example.travelapp.ui.util

import android.app.Activity
import com.google.common.net.MediaType

object FirestoreCollection{
    const val RATING = "rating"
    const val ITINERARIES = "itineraries"
    const val ACTIVITIES = "activities"
    const val USERS = "users"
    const val LOCATION = "location"
    const val ATTRACTION = "attraction"
    const val SCHEDULES = "schedules"
    const val ARTICLES = "articles"
    const val HOTELS = "hotels"

}

object SendBy{
    const val SEND_BY_USER = "user"
    const val SEND_BY_BOT = "bot"
}

object RandomString {
    fun randomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
}

object FireDatabase{
    val TASK = "task"
}

object OkHttpJSON{
    val JSON : MediaType? = MediaType.parse("application/json; charset=utf-8")
}

object API_KEY{
    val CHATGPT_API_KEY = "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"
}
object FireStoreDocumentField {
    val DATE = "date"
    val USER_ID = "user_id"
}

object SharedPrefConstants {
    val LOCAL_SHARED_PREF = "local_shared_pref"
    val FIRST_TIME_ACCESS = "first_time_access"
}

object FirebaseStorageConstants {
    val ROOT_DIRECTORY = "app"

}

enum class HomeTabs(val index: Int, val key: String) {
    NOTES(0, "notes"),
    TASKS(1, "tasks"),
}