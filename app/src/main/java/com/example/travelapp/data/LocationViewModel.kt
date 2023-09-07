package com.example.travelapp.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.travelapp.data.models.LocationItem
import com.example.travelapp.data.repository.ILocationRepository
import kotlinx.coroutines.Dispatchers

class LocationViewModel (
    private val repository: ILocationRepository
): ViewModel() {

    fun getLocations() = liveData(Dispatchers.IO) {
        repository.get().collect {
            emit(it)
        }

    }

    fun getAttractionLocations(item: LocationItem)  {
        repository.getAttractionLocations(item)

    }

    fun getLocationRating(item: LocationItem, updateUi: (Long) -> Unit) {
        repository.getLocationRating(item, updateUi)
    }

    fun checkIfUserRatingExist(uid: String, locationItem: LocationItem, updateUi: (Boolean) -> Unit) {
        repository.checkIfUserRatingExist(uid, locationItem, updateUi)
    }

    fun addUserRating(uid: String, locationItem: LocationItem, updateUi: () -> Unit) {
        repository.addUserRating(uid, locationItem, updateUi)
    }

    fun removeUserRating(uid: String, locationItem: LocationItem, updateUi: () -> Unit) {
        repository.removeUserRating(uid, locationItem, updateUi)
    }

}
