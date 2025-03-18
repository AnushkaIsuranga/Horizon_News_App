<<<<<<< Updated upstream
<<<<<<< Updated upstream
<<<<<<< Updated upstream
<<<<<<< Updated upstream
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
=======
=======
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
package com.kahdse.horizonnewsapp.activity

import android.content.SharedPreferences
import android.os.Bundle
<<<<<<< Updated upstream
<<<<<<< Updated upstream
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.kahdse.horizonnewsapp.R
<<<<<<< Updated upstream
<<<<<<< Updated upstream
<<<<<<< Updated upstream
<<<<<<< Updated upstream
import com.kahdse.horizonnewsapp.activity.LoginActivity

class ProfileActivity : AppCompatActivity() {
    private lateinit var profileImageView: ImageView
    private lateinit var firstNameTextView: TextView
    private lateinit var lastNameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var btnLogout: Button
=======
=======
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes

class ProfileActivity : AppCompatActivity() {

    private lateinit var tvFirstName: TextView
    private lateinit var tvLastName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var imgProfile: ImageView
<<<<<<< Updated upstream
<<<<<<< Updated upstream
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

<<<<<<< Updated upstream
<<<<<<< Updated upstream
<<<<<<< Updated upstream
<<<<<<< Updated upstream
        profileImageView = findViewById(R.id.profileImageView)
        firstNameTextView = findViewById(R.id.firstNameTextView)
        lastNameTextView = findViewById(R.id.lastNameTextView)
        emailTextView = findViewById(R.id.emailTextView)
        btnLogout = findViewById(R.id.btnLogout)
        sharedPref = getSharedPreferences("UserPreferences", MODE_PRIVATE)

        loadUserProfile()

        btnLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun loadUserProfile() {
        val firstName = sharedPref.getString("first_name", "N/A")
        val lastName = sharedPref.getString("last_name", "N/A")
        val email = sharedPref.getString("email", "N/A")
        val profileImageUrl = sharedPref.getString("profile_image", "")

        firstNameTextView.text = "First Name: $firstName"
        lastNameTextView.text = "Last Name: $lastName"
        emailTextView.text = "Email: $email"

        if (!profileImageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(profileImageUrl)
                .placeholder(R.drawable.default_profile)
                .into(profileImageView)
        } else {
            profileImageView.setImageResource(R.drawable.default_profile)
        }
    }

    private fun logoutUser() {
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()

        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
=======
=======
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
        // Initialize views
        tvFirstName = findViewById(R.id.tvFirstName)
        tvLastName = findViewById(R.id.tvLastName)
        tvEmail = findViewById(R.id.tvEmail)
        imgProfile = findViewById(R.id.imgProfile)

        sharedPref = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        loadUserProfile()
    }

    private fun loadUserProfile() {
        val firstName = sharedPref.getString("FirstName", "Unknown") ?: "Unknown"
        val lastName = sharedPref.getString("LastName", "User") ?: "User"
        val email = sharedPref.getString("Email", "No Email") ?: "No Email"
        val profileImageUrl = sharedPref.getString("ProfileImageUrl", null)

        tvFirstName.text = firstName
        tvLastName.text = lastName
        tvEmail.text = email

        // Load profile image (or use default XML image if null)
        if (!profileImageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(profileImageUrl)
                .placeholder(R.drawable.default_profile) // Default placeholder image
                .into(imgProfile)
        } else {
            imgProfile.setImageResource(R.drawable.default_profile) // Default XML image
        }
    }
<<<<<<< Updated upstream
<<<<<<< Updated upstream
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
}
