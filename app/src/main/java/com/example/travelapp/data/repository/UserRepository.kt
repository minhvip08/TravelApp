package com.example.travelapp.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository(private val firebaseFirestore: FirebaseFirestore) : IUserRepository {
    override fun add(uid: String, avatar: String) {
        firebaseFirestore
            .collection("users")
            .document(uid)
            .set(
                hashMapOf(
                    "avatar" to avatar
                )
            ).addOnCompleteListener {
                Log.d("UserRepository", "Successfully added user")
            }.addOnFailureListener {
                Log.w("UserRepository", "Failed to add user")
            }

    }
}