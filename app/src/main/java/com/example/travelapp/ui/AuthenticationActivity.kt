package com.example.travelapp.ui

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.commit
import com.example.travelapp.R
import com.example.travelapp.data.UserViewModel
import com.example.travelapp.data.repository.UserRepository
import com.example.travelapp.ui.fragments.UserInfoFragment
import com.example.travelapp.ui.util.FirebaseStorageConstants
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class AuthenticationActivity : AppCompatActivity() {

    private val TAG = "AuthenticationActivity"

    private val viewModel = UserViewModel(
        UserRepository(FirebaseFirestore.getInstance(), null)
    )

    private lateinit var userInfoFragment: UserInfoFragment
    private lateinit var toggleAuthMode: RadioGroup

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        userInfoFragment = UserInfoFragment.newInstance(false)
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.auth_user_info, userInfoFragment)
        }
    }

    override fun onStart() {
        super.onStart()
        // Get required views
        val authHeader = findViewById<TextView>(R.id.auth_header)
        val authButton = findViewById<Button>(R.id.auth_button)
        val authForgotYourPassword = findViewById<TextView>(R.id.auth_forgot_your_password)
        val authShowPassword = findViewById<CheckBox>(R.id.auth_show_password)
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
            if (userInfoFragment.isEmailInvalid()) {
                Toast.makeText(
                    baseContext,
                    "Email must be valid.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
            else {
                val intent = Intent(this, ResetPasswordActivity::class.java)
                intent.putExtra("email", userInfoFragment.getEmail())
                startActivity(intent)
            }
        }
        // Get RadioGroup object
        toggleAuthMode = findViewById(R.id.toggle_authentication_mode)
        toggleAuthMode.setOnCheckedChangeListener { _, checkedId ->
            // Check which radio button was clicked
            when (checkedId) {
                R.id.radio_button_sign_up -> {
                    authHeader.text = getString(R.string.sign_up)
                    userInfoFragment.showName()
                    authForgotYourPassword.visibility = TextView.GONE
                }
                R.id.radio_button_sign_in -> {
                    authHeader.text = getString(R.string.sign_in)
                    userInfoFragment.hideName()
                    authForgotYourPassword.visibility = TextView.VISIBLE
                }
            }
        }
        // Set "Sign up" or "Sign in" listener
        authButton.setOnClickListener {
            val email = userInfoFragment.getEmail()
            val password = authAttributePassword.text.toString()
            if (toggleAuthMode.checkedRadioButtonId == R.id.radio_button_sign_up) {
                val name = userInfoFragment.getName()
                if (userInfoFragment.isNameInvalid() || userInfoFragment.isEmailInvalid() || authAttributePassword.error != null) {
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
                        .fetchSignInMethodsForEmail(userInfoFragment.getEmail())
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
                                }
                            }
                            else {
                                Log.w("AuthenticationActivity", "fetchSignInMethodsForEmail:failure", it.exception)
                            }
                        }

                }
            }
            else {
                if (userInfoFragment.isEmailInvalid() || authAttributePassword.error != null) {
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
                    viewModel.addUserDocument(user!!.uid)
                    user.updateProfile(userProfileChangeRequest {
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
                    val intent = Intent(this, EmailConfirmationActivity::class.java)
                    intent.putExtra("emailConfirmationType", 0)
                    startActivity(intent)
                    userInfoFragment.clearName()
                    toggleAuthMode.check(R.id.radio_button_sign_in)
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
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
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