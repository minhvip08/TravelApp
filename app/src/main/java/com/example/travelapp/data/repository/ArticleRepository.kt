package com.example.travelapp.data.repository

import android.util.Log
import com.example.travelapp.data.models.ArticleItem
import com.example.travelapp.ui.util.FirestoreCollection
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

private const val POST_PER_REQUEST: Long = 4

class ArticleRepository(
    private val articleDatabase: FirebaseFirestore
) : IArticleRepository {
    private var lastVisible: DocumentSnapshot? = null
    override fun get(updateUi: (List<ArticleItem>) -> Unit) {
        val task = if (lastVisible == null) {
            articleDatabase
                .collection(FirestoreCollection.ARTICLES)
                .orderBy("time", Query.Direction.DESCENDING)
                .limit(POST_PER_REQUEST)
                .get()
        } else {
            articleDatabase
                .collection(FirestoreCollection.ARTICLES)
                .orderBy("time", Query.Direction.DESCENDING)
                .startAfter(lastVisible!!.get("time"))
                .limit(POST_PER_REQUEST)
                .get()
        }
        task.addOnSuccessListener {
            if (it.isEmpty) {
                updateUi(emptyList())
            }
            else {
                lastVisible = it.documents[it.size() - 1]
                updateUi(it.toObjects(ArticleItem::class.java))
            }
        }.addOnFailureListener {
            Log.w("ArticleRepository.get()", "Error getting documents.", it)
        }
    }

    override fun reset() {
        lastVisible = null
    }
}