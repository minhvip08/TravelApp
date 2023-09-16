package com.example.travelapp.data

import androidx.lifecycle.liveData
import com.example.travelapp.data.models.ItineraryItem
import com.example.travelapp.data.models.ScheduleItem
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



    fun setItinerary(uid: String, scheduleId: String, itineraryItem: ItineraryItem, callback: () -> Unit) {
        repository.set(uid, scheduleId, itineraryItem, callback)
    }
}
