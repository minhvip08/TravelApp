package com.example.travelapp.data.repository

import com.example.travelapp.data.models.HotelItem
import kotlinx.coroutines.flow.Flow

interface IHotelRepository {
    fun get(locationId:String): Flow<List<HotelItem>>

}