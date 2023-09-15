package com.example.travelapp.ui

import android.os.Bundle
import android.webkit.WebView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.travelapp.R

class ReadArticleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_article)
        val url = intent.getStringExtra("url")!!
        val webView = findViewById<WebView>(R.id.read_article_web_view)
        webView.loadUrl(url)
        onBackPressedDispatcher.addCallback(this) {
            finish()
        }
    }
}