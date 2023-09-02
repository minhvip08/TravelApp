package com.example.travelapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.example.travelapp.R
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ResetPasswordActivity : AppCompatActivity() {

    private val TAG = "ResetPasswordActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        // Get email address from intent
        var email = intent.getStringExtra("email")
        // Set email address text
        val emailEditText = findViewById<EditText>(R.id.reset_password_attribute_email)
        emailEditText.setText(email)
        // Set Reset password button listener
        findViewById<Button>(R.id.reset_password_button).setOnClickListener {
            email = emailEditText.text.toString()
            if (email.isNullOrEmpty()) {
                emailEditText.error = "Email address is required"
            }
            else {
                resetPassword(email!!)
                val intent = Intent(this, AuthenticationActivity::class.java)
                intent.putExtra("emailConfirmationType", 1)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun resetPassword(email: String) {
        Firebase.auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d(TAG, "sendPasswordResetEmail:success")
            }
            else {
                Log.d(TAG, "sendPasswordResetEmail:failure")
            }
        }
    }
}