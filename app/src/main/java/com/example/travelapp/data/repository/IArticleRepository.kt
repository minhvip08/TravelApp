package com.example.travelapp.data.repository

import com.example.travelapp.data.models.ArticleItem

interface IArticleRepository {

    fun get(updateUi: (List<ArticleItem>) -> Unit)
    fun reset()
}