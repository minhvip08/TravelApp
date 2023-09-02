package com.example.travelapp.data.di

import com.example.travelapp.data.models.LocationItem
import com.example.travelapp.data.repository.IRepository
import com.example.travelapp.data.repository.LocationRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference



object RepositoryModule {

    fun provideLocationRepository(
        database: FirebaseFirestore,
        storageReference: StorageReference
    ): IRepository<LocationItem> {
        return LocationRepository(database,storageReference)
    }


}