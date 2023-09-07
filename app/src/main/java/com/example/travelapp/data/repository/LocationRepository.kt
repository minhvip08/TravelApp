package com.example.travelapp.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.example.travelapp.data.models.Attraction
import com.example.travelapp.data.models.LocationItem
import com.example.travelapp.ui.util.FirestoreCollection
import com.example.travelapp.ui.util.UiState
import com.google.firebase.Timestamp
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.Calendar

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

    override fun getLocationRating(item: LocationItem, updateUi: (Long) -> Unit) {
        locationDatabase.collection(FirestoreCollection.LOCATION)
            .document(item.id)
            .collection(FirestoreCollection.RATING)
            .count()
            .get(AggregateSource.SERVER)
            .addOnSuccessListener {
                val result = it.count
                updateUi(result)
                Log.d(TAG, "getLocationRating: $item.id | $result")
            }
            .addOnFailureListener {
                print(it.message)
            }
    }

    override fun checkIfUserRatingExist(uid: String, locationItem: LocationItem, updateUi: (Boolean) -> Unit) {
        locationDatabase.collection(FirestoreCollection.LOCATION)
            .document(locationItem.id)
            .collection(FirestoreCollection.RATING)
            .document(uid)
            .get()
            .addOnSuccessListener {
                val result = it.exists()
                updateUi(result)
                Log.d(TAG, "checkIfUserRatingExist: true")
            }
            .addOnFailureListener {
                print(it.message)
            }
    }

    override fun addUserRating(uid: String, locationItem: LocationItem, updateUi: () -> Unit) {
        locationDatabase.collection(FirestoreCollection.LOCATION)
            .document(locationItem.id)
            .collection(FirestoreCollection.RATING)
            .document(uid)
            .set(
                hashMapOf(
                    "time" to Timestamp(Calendar.getInstance().time)
                )
            )
            .addOnSuccessListener {
                updateUi()
                Log.d(TAG, "addUserRating: Success")
            }
            .addOnFailureListener {
                print(it.message)
            }
    }

    override fun removeUserRating(uid: String, locationItem: LocationItem, updateUi: () -> Unit) {
        locationDatabase.collection(FirestoreCollection.LOCATION)
            .document(locationItem.id)
            .collection(FirestoreCollection.RATING)
            .document(uid)
            .delete()
            .addOnSuccessListener {
                updateUi()
                Log.d(TAG, "removeUserRating: Success")
            }
            .addOnFailureListener {
                print(it.message)
            }
    }
}