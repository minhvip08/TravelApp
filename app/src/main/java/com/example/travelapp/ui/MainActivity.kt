package com.example.travelapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.travelapp.R
import com.example.travelapp.databinding.ActivityMainBinding
import com.example.travelapp.ui.fragments.ArticlesFragment
import com.example.travelapp.ui.fragments.GuideFragment
import com.example.travelapp.ui.fragments.HomeFragment
import com.example.travelapp.ui.fragments.ScheduleFragment
import com.example.travelapp.ui.util.SharedPrefConstants
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Uncomment the following line to check if the user is signed in
        checkFirstTimeAccess()
        checkCurrentUser()
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())



        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.blogs -> replaceFragment(ArticlesFragment())
                R.id.schedule -> replaceFragment(ScheduleFragment())
                R.id.guide -> replaceFragment(GuideFragment())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment){

        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout_main_activity,fragment)
        transaction.commit()
    }

    private fun checkCurrentUser() {
        val user = Firebase.auth.currentUser
        // Warning: This is a temporary development-only solution to sign out the user
        // Should be replaced with a proper sign out button in the UI
        // Uncomment the following line to sign out the user
        // Firebase.auth.signOut()
        if (user == null) {
            // Go to authentication activity
            val intent = Intent(this, AuthenticationActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun checkFirstTimeAccess(){
        val sharedPref = getSharedPreferences(SharedPrefConstants.FIRST_TIME_ACCESS, MODE_PRIVATE)


        val firstTimeAccess = sharedPref.getBoolean("first_time_access", false)

        if (firstTimeAccess){
            sharedPref.edit().putBoolean("first_time_access", false).apply()
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

}