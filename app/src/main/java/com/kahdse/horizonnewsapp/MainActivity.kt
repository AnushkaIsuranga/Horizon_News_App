package com.kahdse.horizonnewsapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.kahdse.horizonnewsapp.activity.LoginActivity
import com.kahdse.horizonnewsapp.activity.ReporterActivity
import com.kahdse.horizonnewsapp.activity.UserActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize SharedPreferences
        val sharedPref = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
        val userRole = sharedPref.getString("userRole", "")

        if (isLoggedIn) {
            // Redirect to the correct activity based on the stored role
            val intent = when (userRole) {
                "reporter" -> Intent(this, ReporterActivity::class.java)
                "user" -> Intent(this, UserActivity::class.java)
                else -> Intent(this, UserActivity::class.java) // Default to UserActivity if role is not recognized
            }
            startActivity(intent)
        } else {
            // Redirect to UserActivity as a guest
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
        }

        finish()
    }
}