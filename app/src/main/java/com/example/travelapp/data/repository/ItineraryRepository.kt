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

class ItineraryRepository(
    private val itineraryDatabase: FirebaseFirestore
) : IItineraryRepository {
    override fun get(uid: String, itineraryId: String) = flow {
        emit(UiState.Loading)
        emit(
            UiState.Success(
                itineraryDatabase
                    .collection(FirestoreCollection.USERS).document(uid)
                    .collection(FirestoreCollection.SCHEDULES)
                    .document(itineraryId)
                    .collection(FirestoreCollection.ITINERARIES)
                    .get()
                    .await()
                    .documents
                    .mapNotNull {
                        it.toObject(ItineraryItem::class.java)
                    }))
    }.catch {
        emit(UiState.Failure(it.message))
    }


    override fun set(uid: String,
                     scheduleId: String,
                     itineraryItem: ItineraryItem,
                     callback: () -> Unit) {
        itineraryDatabase
            .collection(FirestoreCollection.USERS).document(uid)
            .collection(FirestoreCollection.SCHEDULES)
            .document(scheduleId)
            .collection(FirestoreCollection.ITINERARIES)
            .document(itineraryItem.id)
            .set(itineraryItem)
            .addOnSuccessListener {
                callback()
            }
    }
}