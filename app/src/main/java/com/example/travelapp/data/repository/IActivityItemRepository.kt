package com.example.travelapp.data.repository

import com.example.travelapp.data.models.ActivityItem
import com.example.travelapp.ui.util.UiState
import kotlinx.coroutines.flow.Flow

interface IActivityItemRepository {

    fun get(uid: String, scheduleId: String, itineraryId: String): Flow<UiState<List<ActivityItem>>>

    fun set(uid: String, scheduleId: String, itineraryId: String, activityItem: ActivityItem)
}