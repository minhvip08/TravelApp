package com.example.travelapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.travelapp.R
import com.example.travelapp.databinding.ActivityMainBinding
import com.example.travelapp.ui.adapters.MainViewPagerAdapter
import com.example.travelapp.ui.util.SharedPrefConstants
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MainViewPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Uncomment the following line to check if the user is signed in
        checkFirstTimeAccess()
        checkCurrentUser()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = MainViewPagerAdapter(this)
        binding.viewPagerMainActivity.adapter = adapter
        binding.viewPagerMainActivity.isUserInputEnabled = false
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> binding.viewPagerMainActivity.setCurrentItem(0, false)
                R.id.schedule -> binding.viewPagerMainActivity.setCurrentItem(1, false)
                R.id.guide -> binding.viewPagerMainActivity.setCurrentItem(2, false)
                R.id.feed -> binding.viewPagerMainActivity.setCurrentItem(3, false)
            }
            true
        }
        binding.viewPagerMainActivity.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.bottomNavigation.menu.getItem(position).isChecked = true
            }
        })
    }

    private fun replaceFragment(fragment: Fragment){

        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.view_pager_main_activity,fragment)
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