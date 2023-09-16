package com.example.travelapp.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentManager.TAG
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

class ImageRepository(private val imageStorage: StorageReference): IImageRepository {


    override fun getImage(imageId: String, updateUi: (Bitmap) -> Unit) {
        val storageReference = imageStorage.child("images/$imageId.jpg")
        val localFile = File.createTempFile("images", ".jpg")
        localFile.deleteOnExit()
        storageReference.getFile(localFile).addOnSuccessListener {
//            Toast.makeText(null, "Success + $localFile", Toast.LENGTH_SHORT).show
            CoroutineScope(Dispatchers.IO).launch {
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                withContext(Dispatchers.Main) {
                    updateUi(bitmap)
                }
            }
        }.addOnFailureListener {
            Log.d("ImageRepository.getImage()", "Failed to get image")
        }
    }

    override fun getImagePath(imageId: String, updateUi: (String) -> Unit) {
        val storageReference = imageStorage.child("images/$imageId.jpg")
        val localFile = File.createTempFile("images", ".jpg")
        localFile.deleteOnExit()
        storageReference.getFile(localFile).addOnSuccessListener {
//            Toast.makeText(null, "Success + $localFile", Toast.LENGTH_SHORT).show()
//            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            updateUi(localFile.absolutePath)
        }.addOnFailureListener {
            Log.d("ImageRepository.getImagePath()", "Failed to get image")
        }
    }

    override fun getArticleThumbnail(pid: String, updateUi: (String) -> Unit) {
        val storageReference = imageStorage.child("articles/${pid}_i0.jpg")
        val localFile = File.createTempFile("articles", ".jpg")
        localFile.deleteOnExit()
        storageReference.getFile(localFile).addOnSuccessListener {
            updateUi(localFile.absolutePath)
        }
    }
}