package com.example.travelapp.data.repository

import com.example.travelapp.data.models.ScheduleItem
import com.example.travelapp.ui.util.UiState
import kotlinx.coroutines.flow.Flow

interface IScheduleRepository {
    fun get(uid: String): Flow<UiState<List<ScheduleItem>>>
    fun set(uid: String, scheduleItem: ScheduleItem)
    fun delete(uid: String, scheduleId: String)
}
