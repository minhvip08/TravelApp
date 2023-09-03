package com.example.travelapp.data.repository

import com.example.travelapp.data.models.LocationItem
import com.example.travelapp.ui.util.UiState
import kotlinx.coroutines.flow.Flow

interface ILocationRepository {
    fun get() : Flow<UiState<List<LocationItem>>>

    fun getAttractionLocations(item: LocationItem)

}