package com.kahdse.horizonnewsapp.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kahdse.horizonnewsapp.R

class UserActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("USER_ACTIVITY", "UserActivity started")
        setContentView(R.layout.activity_user)

        sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)

        // Set up Navigation Controller
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Set up Bottom Navigation with NavController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationUser)
        bottomNavigationView.setupWithNavController(navController)

        // Initialize imgProfile
        val imgProfile = findViewById<ImageView>(R.id.imgProfile)
        imgProfile.setOnClickListener {
            val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
            Log.d("USER_ACTIVITY", "Profile clicked. Token: $isLoggedIn")

            if (!isLoggedIn) {
                Log.d("USER_ACTIVITY", "No token found. Redirecting to LoginActivity")
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else {
                Log.d("USER_ACTIVITY", "Token found. Navigating to ProfileFragment")
                navController.navigate(R.id.nav_profile)
            }
        }
    }
}
