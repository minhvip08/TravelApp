package com.example.travelapp.data.repository

import com.example.travelapp.data.models.HotelItem
import com.example.travelapp.ui.util.FirestoreCollection
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class HotelRepository (private val hotelDatabase: FirebaseFirestore): IHotelRepository{
    override fun get(locationId: String, updateUi: (List<HotelItem>) -> Unit) {
        hotelDatabase.collection(FirestoreCollection.HOTELS)
            .whereArrayContains("locationIds", locationId)
            .get()
            .addOnSuccessListener {
                updateUi(it.toObjects(HotelItem::class.java))
            }
    }
}