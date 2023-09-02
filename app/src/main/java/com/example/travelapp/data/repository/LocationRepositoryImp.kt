package com.example.travelapp.data.repository

import android.location.Location
import com.example.travelapp.data.models.LocationItem
import com.example.travelapp.ui.util.FireStoreCollection
import com.example.travelapp.ui.util.UiState
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import java.lang.ref.Reference

class LocationRepositoryImp(
    val locationDatabase: FirebaseFirestore,
    val storageReference: StorageReference

) : LocationRepository{
    override fun addLocation(
        location: LocationItem,
        result: (UiState<Pair<LocationItem, String>>) -> Unit
    ) {

        val document = locationDatabase.collection(FireStoreCollection.LOCATION).document()
        location.id = document.id
        document
            .set(location)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success(Pair(location,"Location has been created successfully"))
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }


    }

    override fun updateLocation(location: LocationItem, result: (UiState<String>) -> Unit) {
        val document = locationDatabase.collection(FireStoreCollection.LOCATION).document(location.id)
        document
            .set(location)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Note has been update successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun deleteLocation(location: LocationItem, result: (UiState<String>) -> Unit) {
        locationDatabase.collection(FireStoreCollection.LOCATION).document(location.id)
            .delete()
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Note has been deleted successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun getLocations(result: (UiState<List<LocationItem>>) -> Unit) {
        locationDatabase.collection(FireStoreCollection.LOCATION)
            .get()
            .addOnSuccessListener { documents ->
                val locations = mutableListOf<LocationItem>()
                for (document in documents) {
                    val location = document.toObject(LocationItem::class.java)
                    locations.add(location)
                }
                result.invoke(
                    UiState.Success(locations)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }


}