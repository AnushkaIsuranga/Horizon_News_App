package com.kahdse.horizonnewsapp.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import androidx.fragment.app.Fragment
import com.kahdse.horizonnewsapp.R
import com.kahdse.horizonnewsapp.activity.LoginActivity

class SettingsFragment : Fragment() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var switchNotifications: Switch
    private lateinit var btnLogout: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        switchNotifications = view.findViewById(R.id.switch_notifications)
        btnLogout = view.findViewById(R.id.btn_logout)

        // Load saved notification preference
        switchNotifications.isChecked = sharedPref.getBoolean("NOTIFICATIONS", true)

        // Toggle notifications
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
        sharedPref.edit().putBoolean("NOTIFICATIONS", isChecked).apply()
    }

        // Logout functionality
        btnLogout.setOnClickListener {
            sharedPref.edit()
                .remove("TOKEN") // Remove login token
                .remove("FirstName") // Clear stored name so guest users don’t see it
                .apply()

            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    return view
    }

    private fun logoutUser() {
        sharedPref.edit().remove("TOKEN").apply() // Remove login token

        // Redirect to LoginActivity
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish() // Close current activity
    }
}