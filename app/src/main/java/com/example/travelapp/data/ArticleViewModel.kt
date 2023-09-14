package com.example.travelapp.data

import com.example.travelapp.data.models.ArticleItem
import com.example.travelapp.data.repository.ArticleRepository

class ArticleViewModel(
    private val repository: ArticleRepository
) {
    fun getArticles(updateUi: (List<ArticleItem>) -> Unit) {
        repository.get(updateUi)
    }

    fun clearArticlesQueue() {
        repository.reset()
    }
}
