package com.example.travelapp.data.repository

import com.example.travelapp.data.models.ActivityItem
import com.example.travelapp.ui.util.FirestoreCollection
import com.example.travelapp.ui.util.UiState
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class ActivityItemRepository(
    private val activityItemDatabase: FirebaseFirestore
) : IActivityItemRepository {
    override fun get(uid: String, scheduleId: String, itineraryId: String) = flow {
        emit(UiState.Loading)
        emit(
            UiState.Success(
                activityItemDatabase
                    .collection(FirestoreCollection.USERS).document(uid)
                    .collection(FirestoreCollection.SCHEDULES).document(scheduleId)
                    .collection(FirestoreCollection.ITINERARIES).document(itineraryId)
                    .collection(FirestoreCollection.ACTIVITIES)
                    .get()
                    .await()
                    .documents
                    .mapNotNull {
                        it.toObject(ActivityItem::class.java)
                    }))
    }.catch {
        emit(UiState.Failure(it.message))
    }

    override fun set(
        uid: String,
        scheduleId: String,
        itineraryId: String,
        activityItem: ActivityItem
    ) {
        activityItemDatabase
            .collection(FirestoreCollection.USERS).document(uid)
            .collection(FirestoreCollection.SCHEDULES).document(scheduleId)
            .collection(FirestoreCollection.ITINERARIES).document(itineraryId)
            .collection(FirestoreCollection.ACTIVITIES)
            .document(activityItem.id)
            .set(activityItem)
    }

    override fun delete(
        uid: String,
        scheduleId: String,
        itineraryId: String,
        activityItem: ActivityItem
    ) {
        activityItemDatabase
            .collection(FirestoreCollection.USERS).document(uid)
            .collection(FirestoreCollection.SCHEDULES).document(scheduleId)
            .collection(FirestoreCollection.ITINERARIES).document(itineraryId)
            .collection(FirestoreCollection.ACTIVITIES)
            .document(activityItem.id)
            .delete()
    }
}