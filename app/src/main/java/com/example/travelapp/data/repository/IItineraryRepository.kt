package com.example.travelapp.data.repository

import com.example.travelapp.data.models.ItineraryItem
import com.example.travelapp.data.models.ScheduleItem
import com.example.travelapp.ui.util.UiState
import kotlinx.coroutines.flow.Flow

interface IItineraryRepository {
    fun get(uid: String, itineraryId: String): Flow<UiState<List<ItineraryItem>>>
    fun set(
        uid: String,
        scheduleId: String,
        itineraryItem: ItineraryItem,
        callback: () -> Unit
    )
}