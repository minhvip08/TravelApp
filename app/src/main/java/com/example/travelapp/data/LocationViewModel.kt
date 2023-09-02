package com.example.travelapp.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.travelapp.data.models.LocationItem
import com.example.travelapp.data.repository.IRepository
import kotlinx.coroutines.Dispatchers

class LocationViewModel (
    private val repository: IRepository<LocationItem>
): ViewModel() {

    fun getLocations() = liveData(Dispatchers.IO) {
        repository.get().collect {
            emit(it)
        }
    }

}
