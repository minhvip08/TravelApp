package com.example.travelapp.data.repository

import android.graphics.Bitmap

interface IImageRepository {
    fun getImage(imageId: String, updateUi: (Bitmap) -> Unit)
    fun getImagePath(imageId: String, updateUi: (String) -> Unit)
}