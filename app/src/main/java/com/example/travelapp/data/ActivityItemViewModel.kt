package com.example.travelapp.data
import androidx.lifecycle.liveData
import com.example.travelapp.data.repository.IActivityItemRepository
import kotlinx.coroutines.Dispatchers

class ActivityItemViewModel (
    private val repository: IActivityItemRepository
) {
    fun getActivities(uid: String, scheduleId: String, itineraryId: String) = liveData(Dispatchers.IO) {
        repository.get(uid, scheduleId, itineraryId).collect {
            emit(it)
        }
    }
}