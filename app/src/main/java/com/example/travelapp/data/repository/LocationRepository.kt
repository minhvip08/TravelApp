package com.example.travelapp.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.example.travelapp.data.models.Attraction
import com.example.travelapp.data.models.LocationItem
import com.example.travelapp.ui.util.FirestoreCollection
import com.example.travelapp.ui.util.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class LocationRepository (
    private val locationDatabase: FirebaseFirestore,
    private val storageReference: StorageReference
) : ILocationRepository {
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


    override fun getAttractionLocations(item: LocationItem) {
        //get list of attractions
        var x = locationDatabase.collection(FirestoreCollection.LOCATION)
            .document(item.id)
            .collection(FirestoreCollection.ATTRACTION)
            .get()
            .addOnSuccessListener {
                item.attraction = it.documents.mapNotNull {
                    it.toObject(Attraction::class.java)
                }

                Log.d(TAG, "getAttractionLocations: ${item.attraction}")

            }
            .addOnFailureListener {
                print(it.message)
            }


    }
}