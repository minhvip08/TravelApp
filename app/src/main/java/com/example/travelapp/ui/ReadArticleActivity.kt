package com.example.travelapp.ui

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.example.travelapp.R
import com.google.android.material.button.MaterialButton

class ReadArticleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_article)
        val url = intent.getStringExtra("url")!!
        val webView = findViewById<WebView>(R.id.read_article_web_view)
        webView.loadUrl(url)
        val backButton = findViewById<MaterialButton>(R.id.read_article_back_button)
        backButton.setOnClickListener {
            finish()
        }
        // If user scroll down gradually hide the back button and vice versa
        webView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY > 0) {
                backButton.animate().alpha(0f).setDuration(200).start()
            } else {
                backButton.animate().alpha(1f).setDuration(200).start()
            }
        }
    }
}