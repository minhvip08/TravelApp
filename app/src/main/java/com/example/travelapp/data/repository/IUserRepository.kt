package com.example.travelapp.data.repository

interface IUserRepository {
    fun add(uid: String, avatar: String)
}