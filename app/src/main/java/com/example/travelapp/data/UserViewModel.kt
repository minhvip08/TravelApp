package com.example.travelapp.data

import android.net.Uri
import com.example.travelapp.data.repository.IUserRepository

class UserViewModel (
    private val repository: IUserRepository
) {
    fun addUserDocument(uid: String) {
        repository.add(uid)
    }

    fun getAvatar(uid: String, updateUi: (String) -> Unit) {
        repository.getAvatar(uid, updateUi)
    }

    fun uploadAvatar(uid: String, uri: Uri, callback: () -> Unit) {
        repository.uploadAvatar(uid, uri, callback)
    }
}