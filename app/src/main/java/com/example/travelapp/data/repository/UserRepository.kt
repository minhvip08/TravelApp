package com.example.travelapp.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import java.io.File

class UserRepository(
    private val firebaseFirestore: FirebaseFirestore,
    private val reference: StorageReference?
) : IUserRepository {
    override fun add(uid: String) {
        firebaseFirestore
            .collection("users")
            .document(uid)
            .set(
                hashMapOf(
                    "uid" to uid
                )
            ).addOnCompleteListener {
                Log.d("UserRepository", "Successfully added user")
            }.addOnFailureListener {
                Log.w("UserRepository", "Failed to add user")
            }

    }

    override fun getAvatar(uid: String, updateUi: (String) -> Unit) {
        val avatarRef = reference!!.child("users/$uid/avatar.jpg")
        val file = File.createTempFile("avatar_", ".jpg")
        file.deleteOnExit()
        avatarRef.getFile(file).addOnSuccessListener {
            updateUi(file.absolutePath)
            Log.d("UserRepository", "Successfully retrieved avatar")
        }.addOnFailureListener {
            if ((it as StorageException).errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                updateUi("")
                Log.w("UserRepository", "No avatar found", it)
            }
            else {
                Log.w("UserRepository", "Failed to retrieve avatar", it)
            }
        }
    }

    override fun uploadAvatar(uid: String, uri: Uri, callback: (Boolean) -> Unit) {
        val avatarRef = reference!!.child("users/$uid/avatar.jpg")
        avatarRef.putFile(uri).addOnSuccessListener {
            callback(true)
            Log.d("UserRepository", "Successfully uploaded avatar")
        }.addOnFailureListener {
            callback(false)
            Log.w("UserRepository", "Failed to upload avatar", it)
        }
    }
}