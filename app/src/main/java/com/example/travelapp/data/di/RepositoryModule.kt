package com.example.travelapp.data.di

import android.content.SharedPreferences
import com.example.travelapp.data.repository.LocationRepository
import com.example.travelapp.data.repository.LocationRepositoryImp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {
    @Provides
    @Singleton
    fun provideLocationRepository(
        database: FirebaseDatabase,
        storageReference: StorageReference
    ): LocationRepository {
        return LocationRepositoryImp(database,storageReference)
    }


}