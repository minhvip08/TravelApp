package com.example.travelapp.data.repository

import com.example.travelapp.data.models.LocationItem
import com.example.travelapp.ui.util.FirestoreCollection
import com.example.travelapp.ui.util.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.tasks.await

class LocationRepository (
    private val locationDatabase: FirebaseFirestore,
    private val storageReference: StorageReference
) : IRepository<LocationItem> {
    override fun get() = flow {
        emit(UiState.Loading)
        emit(UiState.Success(
            locationDatabase
                .collection(FirestoreCollection.LOCATION)
                .get()
                .await()
                .documents
                .mapNotNull {
                    it.toObject(LocationItem::class.java)
                }))
    }.catch {
        emit(UiState.Failure(it.message))
    }

}