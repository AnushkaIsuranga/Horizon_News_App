package com.kahdse.horizonnewsapp.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.kahdse.horizonnewsapp.R
import com.kahdse.horizonnewsapp.activity.LoginActivity

class SettingsFragment : Fragment() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var switchNotifications: Switch
    private lateinit var btnAuth: Button // Renamed to a generic name for both logout/login

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Initialize SharedPreferences
        sharedPref = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Initialize views
        switchNotifications = view.findViewById(R.id.switch_notifications)
        btnAuth = view.findViewById(R.id.btn_logout) // Use the same button for both logout/login

        // Load saved notification preference
        switchNotifications.isChecked = sharedPref.getBoolean("NOTIFICATIONS", true)

        // Toggle notifications
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("NOTIFICATIONS", isChecked).apply()
        }

        // Update button text and behavior based on login state
        updateAuthButton()

        // Set click listener for the button
        btnAuth.setOnClickListener {
            if (isUserLoggedIn()) {
                logoutUser()
            } else {
                loginUser()
            }
        }

        return view
    }

    private fun isUserLoggedIn(): Boolean {
        // Check if the user is logged in
        return sharedPref.getBoolean("isLoggedIn", false)
    }

    private fun updateAuthButton() {
        // Update button text based on login state
        if (isUserLoggedIn()) {
            btnAuth.text = "Logout"
        } else {
            btnAuth.text = "Login"
        }
    }

    private fun logoutUser() {
        // Clear user data from SharedPreferences
        with(sharedPref.edit()) {
            remove("TOKEN")
            remove("FirstName")
            remove("userRole")
            putBoolean("isLoggedIn", false)
            apply()
        }

        // Update button text and behavior
        updateAuthButton()

        // Show a toast message
        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()

        // Redirect to LoginActivity
        loginUser()
    }

    private fun loginUser() {
        // Redirect to LoginActivity
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish() // Close current activity
    }
}