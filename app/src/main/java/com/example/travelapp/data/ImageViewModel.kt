package com.example.travelapp.data

import android.graphics.Bitmap
import com.example.travelapp.data.repository.IImageRepository

class ImageViewModel(
    private val repository: IImageRepository
) {
    fun getImage(imageId: String, updateUi: (Bitmap) -> Unit) {
        repository.getImage(imageId, updateUi)
    }

    fun getImagePath(imageId: String, updateUi: (String) -> Unit) {
        repository.getImagePath(imageId, updateUi)
    }

    fun getArticleThumbnail(pid: String, updateUi: (String) -> Unit) {
        repository.getArticleThumbnail(pid, updateUi)
    }
}