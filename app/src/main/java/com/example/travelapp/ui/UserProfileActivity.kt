package com.example.travelapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.commit
import com.example.travelapp.R
import com.example.travelapp.data.UserViewModel
import com.example.travelapp.data.repository.UserRepository
import com.example.travelapp.ui.fragments.UserInfoFragment
import com.example.travelapp.ui.util.SharedPrefConstants
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class UserProfileActivity : AppCompatActivity() {
    private lateinit var userInfoContainer: FrameLayout
    private lateinit var userInfoFragment: UserInfoFragment
    private lateinit var resetPasswordButton: Button
    private lateinit var updateButton: Button
    private lateinit var signOutButton: Button
    private lateinit var changeAvatarButton: Button
    private lateinit var progressIndicator: CircularProgressIndicator
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
        // Set
        userInfoContainer = findViewById(R.id.user_info_user_profile)
        userInfoFragment = UserInfoFragment.newInstance(true)
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.user_info_user_profile, userInfoFragment)
        }
        // Set sign out button
        signOutButton = findViewById(R.id.button_sign_out_user_profile)
        signOutButton.setOnClickListener {
            signOut()
        }
        changeAvatarButton = findViewById(R.id.button_change_avatar)
        resetPasswordButton = findViewById(R.id.button_reset_password_user_profile)
        updateButton = findViewById(R.id.button_update_user_profile)
        progressIndicator = findViewById(R.id.progress_bar_user_profile)
    }

    override fun onStart() {
        super.onStart()
        userInfoFragment.disableEmail()
        // Set name
        userInfoFragment.setName(Firebase.auth.currentUser!!.displayName.toString())
        // Set email
        userInfoFragment.setEmail(Firebase.auth.currentUser!!.email.toString())
        // Set reset password button
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
        // Set update avatar button
        changeAvatarButton.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            handlePickImage.launch(intent)
        }
    }

    private fun signOut() {
        Firebase.auth.signOut()
        val intent = Intent(this, AuthenticationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        val sharedPref = getSharedPreferences(SharedPrefConstants.FIRST_TIME_ACCESS, MODE_PRIVATE)
        sharedPref.edit().putBoolean("first_time_access", true).apply()
        finish()
    }

    private fun updateUserProfile(name: String) {
        hideEverything()
        progressIndicator.visibility = ProgressBar.VISIBLE
        Firebase.auth.currentUser!!.updateProfile(
            userProfileChangeRequest {
                displayName = name
            }
        ).addOnSuccessListener {
            Log.d("UserProfileActivity", "User profile updated.")
            val intent = Intent()
            intent.putExtra("nameChanged", true)
            setResult(RESULT_OK, intent)
            finish()
        }.addOnFailureListener {
            showEverything()
            progressIndicator.visibility = ProgressBar.GONE
            Toast.makeText(
                baseContext,
                "Failed to update display name.",
                Toast.LENGTH_SHORT,
            ).show()
            Log.d("UserProfileActivity", "User profile update failed.")
        }
    }

    private fun hideEverything() {
        userInfoContainer.visibility = FrameLayout.GONE
        resetPasswordButton.visibility = Button.GONE
        updateButton.visibility = Button.GONE
        signOutButton.visibility = Button.GONE
        changeAvatarButton.visibility = Button.GONE
    }

    private fun showEverything() {
        userInfoContainer.visibility = FrameLayout.VISIBLE
        resetPasswordButton.visibility = Button.VISIBLE
        updateButton.visibility = Button.VISIBLE
        signOutButton.visibility = Button.VISIBLE
        changeAvatarButton.visibility = Button.VISIBLE
    }

    private val handlePickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val result = it.data
            if (result != null) {
                val uri = result.data
                if (uri != null) {
                    hideEverything()
                    progressIndicator.visibility = ProgressBar.VISIBLE
                    userViewModel.uploadAvatar(Firebase.auth.currentUser!!.uid, uri) { isSuccessful ->
                        if (isSuccessful) {
                            progressIndicator.visibility = ProgressBar.GONE
                            val intent = Intent()
                            intent.putExtra("avatarChanged", true)
                            setResult(RESULT_OK, intent)
                            finish()
                        }
                        else {
                            showEverything()
                            progressIndicator.visibility = ProgressBar.GONE
                            Toast.makeText(
                                baseContext,
                                "Failed to upload avatar.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
                }
            }
        }
    }

    companion object {
        private val userViewModel = UserViewModel(UserRepository(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance().reference))
    }
}