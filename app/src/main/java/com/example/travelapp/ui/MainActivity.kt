package com.example.travelapp.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.travelapp.R
import com.example.travelapp.databinding.ActivityMainBinding
import com.example.travelapp.ui.fragments.BlogsFragment
import com.example.travelapp.ui.fragments.GuideFragment
import com.example.travelapp.ui.fragments.HomeFragment
import com.example.travelapp.ui.fragments.ScheduleFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())

        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> replaceFragment(HomeFragment())
                R.id.blogs -> replaceFragment(BlogsFragment())
                R.id.schedule -> replaceFragment(ScheduleFragment())
                R.id.guide -> replaceFragment(GuideFragment())
            }
            true
        }
        // Uncomment the following line to check if the user is signed in
        // checkCurrentUser()
    }

    private fun replaceFragment(fragment: Fragment){

        var fragmentManager = supportFragmentManager
        var transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout_main_activity,fragment)
        transaction.commit()
    }

    private fun checkCurrentUser() {
        val user = Firebase.auth.currentUser
        if (user != null) {
            // User is signed in
        } else {
            // Go to sign in activity
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}