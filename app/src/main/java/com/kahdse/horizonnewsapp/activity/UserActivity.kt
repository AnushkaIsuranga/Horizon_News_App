package com.kahdse.horizonnewsapp.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kahdse.horizonnewsapp.R

class UserActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var tvGreeting: TextView
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        // Initialize shared preferences and TextView
        sharedPref = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        tvGreeting = findViewById(R.id.tvGreeting)

        // Fetch user's name from SharedPreferences and update greeting text
        val userName = sharedPref.getString("FirstName", null)
        tvGreeting.text = if (userName != null) "Hey $userName!" else "Hey Guest!"

        // Set up the NavController for Bottom Navigation
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationUser)
        bottomNavigationView.setupWithNavController(navController)

        // Handle profile image click
        val imgProfile = findViewById<ImageView>(R.id.imgProfile)
        imgProfile.setOnClickListener {
            val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
            if (!isLoggedIn) {
                // If not logged in, open the login screen
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                // If logged in, open the profile activity
                startActivity(Intent(this, ProfileActivity::class.java))
            }
        }
    }
}
