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
import com.kahdse.horizonnewsapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kahdse.horizonnewsapp.fragment.CreateReportFragment

class ReporterActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var tvGreeting: TextView
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reporter)

        sharedPref = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        // Initialize TextView correctly
        tvGreeting = findViewById(R.id.tvGreeting)

        // Fetch reporter's first name
        val FirstName = getReporterFirstName()

        // Update the TextView with the actual name
        tvGreeting.text = "Hey $FirstName!"

        // Set up Navigation Controller
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Set up Bottom Navigation with NavController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationReporter)
        bottomNavigationView.setupWithNavController(navController)

        // Set up floating action button
        val fabCreateReport = findViewById<FloatingActionButton>(R.id.btnCreateReport)
        fabCreateReport.setOnClickListener {
            val createReportFragment = CreateReportFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, createReportFragment)  // Replace container with new fragment
            transaction.addToBackStack(null)  // Allows back navigation
            transaction.commit()
        }

        // Initialize imgProfile
        val imgProfile = findViewById<ImageView>(R.id.imgProfile)
        imgProfile.setOnClickListener {
            val isLoggedIn = sharedPref.contains("TOKEN") // ✅ Check if token exists
            Log.d("REPORTER_ACTIVITY", "Profile clicked. Logged in: $isLoggedIn")

            if (!isLoggedIn) {
                Log.d("REPORTER_ACTIVITY", "No token found. Redirecting to LoginActivity")
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else {
                Log.d("REPORTER_ACTIVITY", "Token found. Navigating to ProfileFragment")
                navController.navigate(R.id.nav_profile) // ✅ Correct navigation
            }
        }
    }

    private fun getReporterFirstName(): String {
        // Retrieve first name from SharedPreferences
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("FirstName", "reporter") ?: "reporter"
    }
}
