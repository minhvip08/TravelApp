package com.example.travelapp.data.repository

import com.example.travelapp.data.models.ScheduleItem

interface IScheduleRepository {
    fun get(uid: String, updateUi: (List<ScheduleItem>) -> Unit)
    fun delete(uid: String, scheduleId: String, updateUi: () -> Unit)
    fun set(uid: String, scheduleItem: ScheduleItem, callback: () -> Unit)
}