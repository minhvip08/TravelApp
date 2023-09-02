package com.example.travelapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.travelapp.data.models.LocationItem
import com.example.travelapp.data.repository.LocationRepository
import com.example.travelapp.ui.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    val repository: LocationRepository
): ViewModel() {
    private val _locations = MutableLiveData<UiState<List<LocationItem>>>(UiState.Loading)
    val locationItem: LiveData<UiState<List<LocationItem>>>
        get() = _locations

    private val _addLocation = MutableLiveData<UiState<Pair<LocationItem,String>>>()
    val addLocation: LiveData<UiState<Pair<LocationItem,String>>>
        get() = _addLocation

    private val _updateLocation = MutableLiveData<UiState<String>>()
    val updateLocation: LiveData<UiState<String>>
        get() = _updateLocation

    private val _deleteLocation = MutableLiveData<UiState<String>>()
    val deleteLocation: LiveData<UiState<String>>
        get() = _deleteLocation

    fun getLocations() {
        _locations.value = UiState.Loading
        repository.getLocations { _locations.value = it }
    }

    fun addLocation(location: LocationItem){
        _addLocation.value = UiState.Loading
        repository.addLocation(location) { _addLocation.value = it }
    }

    fun updateLocation(location: LocationItem){
        _updateLocation.value = UiState.Loading
        repository.updateLocation(location) { _updateLocation.value = it }
    }

    fun deleteLocation(location: LocationItem){
        _deleteLocation.value = UiState.Loading
        repository.deleteLocation(location) { _deleteLocation.value = it }
    }




}