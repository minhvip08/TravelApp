package com.example.travelapp.ui.util

object FirestoreCollection{
    val LOCATION = "location"
    val ATTRACTION = "attraction"

}

object FireDatabase{
    val TASK = "task"
}

object FireStoreDocumentField {
    val DATE = "date"
    val USER_ID = "user_id"
}

object SharedPrefConstants {
    val LOCAL_SHARED_PREF = "local_shared_pref"
    val USER_SESSION = "user_session"
}

object FirebaseStorageConstants {
    val ROOT_DIRECTORY = "app"

}

enum class HomeTabs(val index: Int, val key: String) {
    NOTES(0, "notes"),
    TASKS(1, "tasks"),
}