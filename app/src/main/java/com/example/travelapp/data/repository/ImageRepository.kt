package com.example.travelapp.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentManager.TAG
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.io.File

class ImageRepository(private val imageStorage: StorageReference): IImageRepository {


    override fun getImage(imageId: String, updateUi: (Bitmap) -> Unit) {
        val storageReference = imageStorage.child("images/$imageId.jpg")
        val localFile = File.createTempFile("images", "jpg")
        storageReference.getFile(localFile).addOnSuccessListener {
//            Toast.makeText(null, "Success + $localFile", Toast.LENGTH_SHORT).show()
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            updateUi(bitmap)
        }.addOnFailureListener {
            Toast.makeText(null, "Failed to get image", Toast.LENGTH_SHORT).show()
        }

    }

    override fun getImagePath(imageId: String, updateUi: (String) -> Unit) {
        val storageReference = imageStorage.child("images/$imageId.jpg")
        val localFile = File.createTempFile("images", "jpg")
        storageReference.getFile(localFile).addOnSuccessListener {
//            Toast.makeText(null, "Success + $localFile", Toast.LENGTH_SHORT).show()
//            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            updateUi(localFile.absolutePath)
        }.addOnFailureListener {
            Toast.makeText(null, "Failed to get image", Toast.LENGTH_SHORT).show()
        }
    }
}