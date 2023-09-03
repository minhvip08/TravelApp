package com.example.travelapp.ui

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.travelapp.R
import com.example.travelapp.data.UserViewModel
import com.example.travelapp.data.repository.UserRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class AuthenticationActivity : AppCompatActivity() {

    private val TAG = "AuthenticationActivity"

    private val viewModel = UserViewModel(
        UserRepository(
            FirebaseFirestore.getInstance()
        )
    )

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        // Get required views
        val authHeader = findViewById<TextView>(R.id.auth_header)
        val authAttributeHeaderName = findViewById<TextView>(R.id.auth_attribute_header_name)
        val authAttributeLayoutName = findViewById<LinearLayout>(R.id.auth_attribute_layout_name)
        val authButton = findViewById<Button>(R.id.auth_button)
        val authForgotYourPassword = findViewById<TextView>(R.id.auth_forgot_your_password)
        val authShowPassword = findViewById<CheckBox>(R.id.auth_show_password)
        val authAttributeName = findViewById<EditText>(R.id.auth_attribute_name)
        val authAttributeEmail = findViewById<EditText>(R.id.auth_attribute_email)
        val authAttributePassword = findViewById<EditText>(R.id.auth_attribute_password)

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

        // After text changes
        authAttributeName.doAfterTextChanged {
            if (it.isNullOrEmpty()) {
                authAttributeName.error = "Name is required"
            }
            else if (it.isBlank()) {
                authAttributeName.error = "Name must not be blank"
            }
            else if (it.length < 2) {
                authAttributeName.error = "Name must be at least 2 characters long"
            }
            else if (it.length > 50) {
                authAttributeName.error = "Name must not exceed 50 characters"
            }
        }
        authAttributeEmail.doAfterTextChanged {
            if (it.isNullOrEmpty()) {
                authAttributeEmail.error = "Email address is required"
            }
            else if (it.isBlank()) {
                authAttributeEmail.error = "Email address must not be blank"
            }
            else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()) {
                authAttributeEmail.error = "Invalid email address"
            }
        }
        authAttributePassword.doAfterTextChanged {
            if (it.isNullOrEmpty()) {
                authAttributePassword.error = "Password is required"
            }
            else if (it.isBlank()) {
                authAttributePassword.error = "Password must not be blank"
            }
            else if (it.length < 6) {
                authAttributePassword.error = "Password must be at least 6 characters long"
            }
            else if (it.length > 30) {
                authAttributePassword.error = "Password must not exceed 30 characters"
            }
        }
        // Set "Forgot your password?" listener
        authForgotYourPassword.setOnClickListener {
            if (authAttributeEmail.error != null) {
                Toast.makeText(
                    baseContext,
                    "Email must be valid.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
            else {
                val intent = Intent(this, ResetPasswordActivity::class.java)
                intent.putExtra("email", authAttributeEmail.text.toString())
                startActivity(intent)
            }
        }
        // Get RadioGroup object
        val toggleAuthMode = findViewById<RadioGroup>(R.id.toggle_authentication_mode)
        toggleAuthMode.setOnCheckedChangeListener { _, checkedId ->
            // Check which radio button was clicked
            when (checkedId) {
                R.id.radio_button_sign_up -> {
                    authHeader.text = getString(R.string.sign_up)
                    authAttributeHeaderName.visibility = TextView.VISIBLE
                    authAttributeLayoutName.visibility = LinearLayout.VISIBLE
                    authForgotYourPassword.visibility = TextView.GONE
                }
                R.id.radio_button_sign_in -> {
                    authHeader.text = getString(R.string.sign_in)
                    authAttributeHeaderName.visibility = TextView.GONE
                    authAttributeLayoutName.visibility = LinearLayout.GONE
                    authForgotYourPassword.visibility = TextView.VISIBLE
                }
            }
        }
        // Set "Sign up" or "Sign in" listener
        authButton.setOnClickListener {
            val email = authAttributeEmail.text.toString()
            val password = authAttributePassword.text.toString()
            if (toggleAuthMode.checkedRadioButtonId == R.id.radio_button_sign_up) {
                val name = authAttributeName.text.toString()
                if (authAttributeName.error != null || authAttributeEmail.error != null || authAttributePassword.error != null) {
                    Toast.makeText(
                        baseContext,
                        "Please fix the errors above.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                else if (name.isBlank() || email.isBlank() || password.isBlank()) {
                    Toast.makeText(
                        baseContext,
                        "Please fill in all the fields.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                else {
                    Firebase.auth
                        .fetchSignInMethodsForEmail(authAttributeEmail.text.toString())
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                if (it.result?.signInMethods?.isNotEmpty() == true) {
                                    Toast.makeText(
                                        baseContext,
                                        "Email address is already registered.",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                }
                                else {
                                    createAccount(name, email, password)
                                    val intent = Intent(this, EmailConfirmationActivity::class.java)
                                    intent.putExtra("emailConfirmationType", 0)
                                    startActivity(intent)
                                    authAttributeName.text.clear()
                                    toggleAuthMode.check(R.id.radio_button_sign_in)
                                }
                            }
                            else {
                                Log.w("AuthenticationActivity", "fetchSignInMethodsForEmail:failure", it.exception)
                            }
                        }

                }
            }
            else {
                if (authAttributeEmail.error != null || authAttributePassword.error != null) {
                    Toast.makeText(
                        baseContext,
                        "Please fix the errors above.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                else if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(
                        baseContext,
                        "Please fill in all the fields.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                else {
                    signIn(email, password)
                    if (Firebase.auth.currentUser != null) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    private fun createAccount(name: String, email: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = Firebase.auth.currentUser
                    viewModel.addUserDocument(user!!.uid, "")
                    user!!.updateProfile(userProfileChangeRequest {
                        displayName = name
                    }).addOnCompleteListener(this) {
                        if (it.isSuccessful) {
                            Log.d(TAG, "updateProfile:success")
                        }
                        else {
                            Log.w(TAG, "updateProfile:failure", it.exception)
                        }
                    }
                    user.sendEmailVerification()
                        .addOnCompleteListener(this) {
                            if (it.isSuccessful) {
                                Log.d(TAG, "sendEmailVerification:success")
                            }
                            else {
                                Log.w(TAG, "sendEmailVerification:failure", it.exception)
                                Toast.makeText(
                                    baseContext,
                                    "Unable to send verification email.",
                                    Toast.LENGTH_SHORT,
                                ).show()
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