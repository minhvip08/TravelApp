package com.example.travelapp.data.repository

import android.location.Location
import com.example.travelapp.data.models.LocationItem
import com.example.travelapp.ui.util.UiState

interface LocationRepository {
    fun addLocation(location: LocationItem, result: (UiState<Pair<LocationItem, String>>) -> Unit)
    fun updateLocation(location: LocationItem, result: (UiState<String>) -> Unit)
    fun deleteLocation(location: LocationItem, result: (UiState<String>) -> Unit)
    fun getLocations(result: (UiState<List<LocationItem>>) -> Unit)

}