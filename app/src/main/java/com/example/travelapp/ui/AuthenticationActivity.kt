package com.example.travelapp.ui

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.travelapp.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

class AuthenticationActivity : AppCompatActivity() {

    private val TAG = "AuthenticationActivity"

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        // Get required views
        val authHeader = findViewById<TextView>(R.id.auth_header)
        val authAttributeHeaderName = findViewById<TextView>(R.id.auth_attribute_header_name)
        val authAttributeLayoutName = findViewById<LinearLayout>(R.id.auth_attribute_layout_name)
        val authButton = findViewById<Button>(R.id.auth_button)
        val authPasswordReset = findViewById<TextView>(R.id.auth_forgot_your_password)
        val authAttributeEmail = findViewById<EditText>(R.id.auth_attribute_email)
        val authAttributePassword = findViewById<EditText>(R.id.auth_attribute_password)
        val authAttributeName = findViewById<EditText>(R.id.auth_attribute_name)
        val authShowPassword = findViewById<CheckBox>(R.id.auth_show_password)

        // Set show password checkbox listener
        authShowPassword.setOnCheckedChangeListener { _, isChecked ->
            val start = authAttributePassword.selectionStart
            val end = authAttributePassword.selectionEnd
            if (isChecked) {
                authAttributePassword.transformationMethod = null
            } else {
                authAttributePassword.transformationMethod = PasswordTransformationMethod()
            }
            authAttributePassword.setSelection(start, end)
        }

        // Get RadioGroup object
        val toggleAuthMode = findViewById<RadioGroup>(R.id.toggle_authentication_mode)
        toggleAuthMode.setOnCheckedChangeListener { group, checkedId ->
            // Check which radio button was clicked
            when (checkedId) {
                R.id.radio_button_sign_up -> {
                    authHeader.text = getString(R.string.sign_up)
                    authAttributeHeaderName.visibility = TextView.VISIBLE
                    authAttributeLayoutName.visibility = LinearLayout.VISIBLE
                    authPasswordReset.visibility = TextView.GONE
                    authButton.setOnClickListener {
                        createAccount(
                            authAttributeName.text.toString(),
                            authAttributeEmail.text.toString(),
                            authAttributePassword.text.toString(),
                        )
                        if (Firebase.auth.currentUser != null) {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
                R.id.radio_button_sign_in -> {
                    authHeader.text = getString(R.string.sign_in)
                    authAttributeHeaderName.visibility = TextView.GONE
                    authAttributeLayoutName.visibility = LinearLayout.GONE
                    authPasswordReset.visibility = TextView.VISIBLE
                    authButton.setOnClickListener {
                        signIn(
                            authAttributeEmail.text.toString(),
                            authAttributePassword.text.toString(),
                        )
                        if (Firebase.auth.currentUser != null) {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun createAccount(name: String, email: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = Firebase.auth.currentUser
                    user!!.updateProfile(userProfileChangeRequest {
                        displayName = name
                    }).addOnCompleteListener(this) {
                        if (it.isSuccessful) {
                            Log.d(TAG, "updateProfile:success")
                        }
                        else {
                            Log.d(TAG, "updateProfile:failure")
                        }
                    }
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private fun signIn(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }
}