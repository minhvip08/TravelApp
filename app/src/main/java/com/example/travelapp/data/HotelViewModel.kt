package com.example.travelapp.data

import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.liveData
import com.example.travelapp.data.models.HotelItem
import com.example.travelapp.data.repository.IHotelRepository
import kotlinx.coroutines.Dispatchers

class HotelViewModel(
    private val repository: IHotelRepository) {

    fun getHotels(locationId: String, updateUi: (List<HotelItem>) -> Unit) {
        repository.get(locationId, updateUi)
    }


}