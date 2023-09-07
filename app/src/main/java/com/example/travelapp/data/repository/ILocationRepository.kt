package com.example.travelapp.data.repository

import com.example.travelapp.data.models.LocationItem
import com.example.travelapp.ui.util.UiState
import kotlinx.coroutines.flow.Flow

interface ILocationRepository {
    fun get() : Flow<UiState<List<LocationItem>>>

    fun getAttractionLocations(item: LocationItem)

    fun addUserRating(uid: String, locationItem: LocationItem, updateUi: () -> Unit)
    fun removeUserRating(uid: String, locationItem: LocationItem, updateUi: () -> Unit)
    fun getLocationRating(item: LocationItem, updateUi: (Long) -> Unit)
    fun checkIfUserRatingExist(
        uid: String,
        locationItem: LocationItem,
        updateUi: (Boolean) -> Unit
    )
}