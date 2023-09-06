package com.example.travelapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.travelapp.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EmailConfirmationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_confirmation)
        // Get email confirmation type
        val emailConfirmationType = intent.getIntExtra("emailConfirmationType", 0)
        if (emailConfirmationType > 0) {
            findViewById<ImageView>(R.id.image_email_confirmation)
                .setImageResource(R.drawable.baseline_key_22)
            findViewById<TextView>(R.id.text_email_confirmation)
                .text = getString(R.string.email_confirmation_reset)
        }
        // Return to sign in activity
        findViewById<Button>(R.id.button_return).setOnClickListener {
            if (emailConfirmationType == 2) {
                Firebase.auth.signOut()
                val intentAuth = Intent(this, AuthenticationActivity::class.java)
                intentAuth.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intentAuth)
            }
            else {
                finish()
            }
        }
    }
}