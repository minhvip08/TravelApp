package com.example.travelapp.data.di

import android.content.SharedPreferences
import com.example.travelapp.data.repository.LocationRepository
import com.example.travelapp.data.repository.LocationRepositoryImp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference



object RepositoryModule {

    fun provideLocationRepository(
        database: FirebaseFirestore,
        storageReference: StorageReference
    ): LocationRepository {
        return LocationRepositoryImp(database,storageReference)
    }


}