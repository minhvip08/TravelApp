package com.example.travelapp.data.repository

import com.example.travelapp.data.models.ItineraryItem
import com.example.travelapp.data.models.ScheduleItem
import com.example.travelapp.ui.util.FirestoreCollection
import com.example.travelapp.ui.util.UiState
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class ScheduleRepository(
    private val scheduleDatabase: FirebaseFirestore
) : IScheduleRepository {
    override fun get(uid: String) = flow {
        emit(UiState.Loading)
        emit(
            UiState.Success(
            scheduleDatabase
                .collection(FirestoreCollection.USERS).document(uid)
                .collection(FirestoreCollection.SCHEDULES)
                .orderBy("startDate")
                .get()
                .await()
                .documents
                .mapNotNull {
                    it.toObject(ScheduleItem::class.java)
                }))
    }.catch {
        emit(UiState.Failure(it.message))
    }

    override fun set(uid: String, scheduleItem: ScheduleItem, callback: () -> Unit) {
        scheduleDatabase
            .collection(FirestoreCollection.USERS).document(uid)
            .collection(FirestoreCollection.SCHEDULES)
            .document(scheduleItem.id)
            .set(scheduleItem)
            .addOnSuccessListener {
                callback()
            }
    }

    override fun delete(uid: String, scheduleId: String) {
        scheduleDatabase
            .collection(FirestoreCollection.USERS).document(uid)
            .collection(FirestoreCollection.SCHEDULES)
            .document(scheduleId)
            .delete()
    }
}