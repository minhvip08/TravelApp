package com.example.travelapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.example.travelapp.R
import androidx.activity.addCallback
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ResetPasswordActivity : AppCompatActivity() {

    private val TAG = "ResetPasswordActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        // get emailConfirmationType from intent
        val emailConfirmationType = intent.getIntExtra("emailConfirmationType", 1)
        // Get email address from intent
        var email = intent.getStringExtra("email")
        // Set email address text
        val emailEditText = findViewById<EditText>(R.id.reset_password_attribute_email)
        emailEditText.setText(email)
        // After text change check if email address is valid
        emailEditText.doAfterTextChanged {
            if (it.isNullOrEmpty()) {
                emailEditText.error = "Email address is required"
            }
            else if (it.isBlank()) {
                emailEditText.error = "Email address must not be blank"
            }
            else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()) {
                emailEditText.error = "Invalid email address"
            }
        }
        // Set Reset password button listener
        findViewById<Button>(R.id.reset_password_button).setOnClickListener {
            if (emailEditText.error != null) {
                Toast.makeText(
                    baseContext,
                    "Email must be valid.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
            else {
                email = emailEditText.text.toString()
                Firebase.auth
                    .fetchSignInMethodsForEmail(email!!)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            if (it.result?.signInMethods?.isEmpty() == true) {
                                Toast.makeText(
                                    baseContext,
                                    "Email address not found.",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                            else {
                                resetPassword(email!!)
                                val intent = Intent(this, EmailConfirmationActivity::class.java)
                                intent.putExtra("emailConfirmationType", emailConfirmationType)
                                startActivity(intent)
                                finish()
                            }
                        }
                        else {
                            Log.w("ResetPasswordActivity", "fetchSignInMethodsForEmail:failure", it.exception)
                        }
                    }
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