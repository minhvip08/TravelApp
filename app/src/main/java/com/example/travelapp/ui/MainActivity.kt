package com.example.travelapp.ui

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.travelapp.R
import com.example.travelapp.databinding.ActivityMainBinding
import com.example.travelapp.ui.fragments.BlogsFragment
import com.example.travelapp.ui.fragments.GuideFragment
import com.example.travelapp.ui.fragments.HomeFragment
import com.example.travelapp.ui.fragments.ScheduleFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())

        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.home -> replaceFragment(HomeFragment())
                R.id.blogs -> replaceFragment(BlogsFragment())
                R.id.schedule -> replaceFragment(ScheduleFragment())
                R.id.guide -> replaceFragment(GuideFragment())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment){

        var fragmentManager = supportFragmentManager
        var transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout_main_activity,fragment)
        transaction.commit()
    }
}