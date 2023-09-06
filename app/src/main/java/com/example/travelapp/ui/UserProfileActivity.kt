package com.example.travelapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.commit
import com.example.travelapp.R
import com.example.travelapp.data.UserViewModel
import com.example.travelapp.data.repository.UserRepository
import com.example.travelapp.ui.fragments.UserInfoFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class UserProfileActivity : AppCompatActivity() {
    private lateinit var userInfoFragment: UserInfoFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        // Set toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar_user_profile)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        // Set fragment
        userInfoFragment = UserInfoFragment.newInstance(true)
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.user_info_user_profile, userInfoFragment)
        }
    }

    override fun onStart() {
        super.onStart()
        userInfoFragment.disableEmail()
        // Set name
        userInfoFragment.setName(Firebase.auth.currentUser!!.displayName.toString())
        // Set email
        userInfoFragment.setEmail(Firebase.auth.currentUser!!.email.toString())
        // Set reset password button
        val resetPasswordButton = findViewById<Button>(R.id.button_reset_password_user_profile)
        resetPasswordButton.setOnClickListener {
            if (userInfoFragment.isEmailInvalid()) {
                Toast.makeText(
                    baseContext,
                    "Email must be valid.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
            else {
                val intentResetPassword = Intent(this, ResetPasswordActivity::class.java)
                intentResetPassword.putExtra("email", userInfoFragment.getEmail())
                intentResetPassword.putExtra("emailConfirmationType", 2)
                startActivity(intentResetPassword)
            }
        }
        // Set update button
        val updateButton = findViewById<Button>(R.id.button_update_user_profile)
        updateButton.setOnClickListener {
            val name = userInfoFragment.getName()
            if (userInfoFragment.isNameInvalid()) {
                Toast.makeText(
                    baseContext,
                    "Please fix the errors above.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
            else if (name.isBlank()) {
                Toast.makeText(
                    baseContext,
                    "Please fill in all the fields.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
            else {
                updateUserProfile(name)
            }
        }
        // Set sign out button
        val signOutButton = findViewById<Button>(R.id.button_sign_out_user_profile)
        signOutButton.setOnClickListener {
            signOut()
            finish()
        }
    }

    private fun signOut() {
        Firebase.auth.signOut()
        val intent = Intent(this, AuthenticationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun updateUserProfile(name: String) {
        Firebase.auth.currentUser!!.updateProfile(
            userProfileChangeRequest {
                displayName = name
            }
        ).addOnCompleteListener {
            Log.d("UserProfileActivity", "User profile updated.")
            finish()
        }.addOnFailureListener {
            Log.d("UserProfileActivity", "User profile update failed.")
        }
    }
}