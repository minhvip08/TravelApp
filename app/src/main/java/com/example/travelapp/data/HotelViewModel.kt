package com.example.travelapp.data

import com.example.travelapp.data.models.HotelItem
import com.example.travelapp.data.repository.IHotelRepository

class HotelViewModel(
    private val repository: IHotelRepository) {

    fun getHotels(locationId: String, updateUi: (List<HotelItem>) -> Unit) {
        repository.get(locationId, updateUi)
    }


}