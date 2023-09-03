package com.example.travelapp.data

import com.example.travelapp.data.repository.IUserRepository

class UserViewModel (
    private val repository: IUserRepository
) {
    fun addUserDocument(uid: String, avatar: String) {
        repository.add(uid, avatar)
    }
}