package com.example.travelapp.data

import androidx.lifecycle.liveData
import com.example.travelapp.data.models.ScheduleItem
import com.example.travelapp.data.repository.IScheduleRepository
import kotlinx.coroutines.Dispatchers

class ScheduleViewModel(
    private val repository: IScheduleRepository
) {
    fun getSchedules(uid: String) = liveData(Dispatchers.IO) {
        repository.get(uid).collect {
            emit(it)
        }
    }

    fun setSchedule(uid: String, scheduleItem: ScheduleItem) {
        repository.set(uid, scheduleItem)
    }
}