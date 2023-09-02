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
    val locationDatabase: FirebaseDatabase,
    val storageReference: StorageReference

) : LocationRepository{
    override fun addLocation(
        location: LocationItem,
        result: (UiState<Pair<LocationItem, String>>) -> Unit
    ) {
        val reference = locationDatabase.reference.child(FireStoreCollection.LOCATION).push()
        val uniqueKey = reference.key ?: "invalid"
        location.id = uniqueKey
        reference
            .setValue(location)
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
        val reference = locationDatabase.reference.child(FireStoreCollection.LOCATION).child(location.id)
        reference
            .setValue(location)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Location has been updated successfully")
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
        val reference = locationDatabase.reference.child(FireStoreCollection.LOCATION).child(location.id)
        reference
            .removeValue()
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Location has been deleted successfully")
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
        val reference = locationDatabase.reference.child(FireStoreCollection.LOCATION)
        reference.get()
            .addOnSuccessListener {
                val locations = arrayListOf<LocationItem?>()
                for (item in it.children){
                    val location = item.getValue(LocationItem::class.java)
                    locations.add(location)
                }
                result.invoke(UiState.Success(locations.filterNotNull()))
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