package com.example.travelapp.data.repository

import com.example.travelapp.data.models.ItineraryItem
import com.example.travelapp.data.models.ScheduleItem
import com.example.travelapp.ui.util.UiState
import kotlinx.coroutines.flow.Flow

interface IScheduleRepository {
    fun get(uid: String, updateUi: (MutableList<ScheduleItem>) -> Unit)
    fun delete(uid: String, scheduleId: String, updateUi: () -> Unit)
    fun set(uid: String, scheduleItem: ScheduleItem, callback: () -> Unit)
}
