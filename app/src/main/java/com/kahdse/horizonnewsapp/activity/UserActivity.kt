package com.kahdse.horizonnewsapp.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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

        sharedPref = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val userName = sharedPref.getString("FirstName", null)

        // Initialize TextView correctly
        tvGreeting = findViewById(R.id.tvGreeting)

        // Update the TextView with the actual name
        tvGreeting.text = if (userName != null) "Hey $userName!" else "Hey Guest!"

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
            Log.d("USER_ACTIVITY", "Profile clicked. isLoggedIn: $isLoggedIn")

            if (!isLoggedIn) {
                Log.d("USER_ACTIVITY", "User is not logged in. Redirecting to LoginActivity")
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else {
                Log.d("USER_ACTIVITY", "User is logged in. Navigating to ProfileFragment")
                navController.navigate(R.id.nav_profile) // 🔍 Make sure this ID is correct in `nav_graph.xml`
            }
        }
    }
}
