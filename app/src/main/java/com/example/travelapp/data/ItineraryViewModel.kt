package com.example.travelapp.data

import androidx.lifecycle.liveData
import com.example.travelapp.data.repository.IItineraryRepository
import kotlinx.coroutines.Dispatchers

class ItineraryViewModel(
    private val repository: IItineraryRepository
) {
    fun getItineraries(uid: String, scheduleId: String) = liveData(Dispatchers.IO) {
        repository.get(uid, scheduleId).collect {
            emit(it)
        }
    }
}
