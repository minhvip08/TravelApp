package com.example.travelapp.data

import androidx.lifecycle.liveData
import com.example.travelapp.data.models.ScheduleItem
import com.example.travelapp.data.repository.IScheduleRepository
import kotlinx.coroutines.Dispatchers

class ScheduleViewModel(
    private val repository: IScheduleRepository
) {
    fun getSchedules(uid: String, updateUi: (List<ScheduleItem>) -> Unit) {
        repository.get(uid, updateUi)
    }

    fun setSchedule(uid: String, scheduleItem: ScheduleItem, callback:() -> Unit) {
        repository.set(uid, scheduleItem, callback)
    }

    fun delete(uid: String, scheduleId: String, updateUi: () -> Unit) {
        repository.delete(uid, scheduleId, updateUi)
    }
}