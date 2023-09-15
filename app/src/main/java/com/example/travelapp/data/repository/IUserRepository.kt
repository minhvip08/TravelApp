package com.example.travelapp.data.repository

import android.net.Uri

interface IUserRepository {
    fun add(uid: String)
    fun getAvatar(uid: String, updateUi: (String) -> Unit)
    fun uploadAvatar(uid: String, uri: Uri, callback: () -> Unit)
}