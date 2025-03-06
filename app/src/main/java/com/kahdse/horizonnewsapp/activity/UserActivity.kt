<<<<<<< Updated upstream
package com.kahdse.horizonnewsapp.activity

=======
>>>>>>> Stashed changes
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kahdse.horizonnewsapp.R
import com.kahdse.horizonnewsapp.activity.LoginActivity

class UserActivity : AppCompatActivity() {

    private lateinit var navController: NavController
<<<<<<< Updated upstream
=======
    private lateinit var tvGreeting: TextView
    private lateinit var imgProfile: ImageView
>>>>>>> Stashed changes
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("USER_ACTIVITY", "UserActivity started")
        setContentView(R.layout.activity_user)

<<<<<<< Updated upstream
        sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
=======
        sharedPref = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val userName = sharedPref.getString("FirstName", "User") ?: "User"
        val profilePicUrl = sharedPref.getString("ProfilePicUrl", "")

        tvGreeting = findViewById(R.id.tvGreeting)
        imgProfile = findViewById(R.id.imgProfile)

        val greeting = getGreeting()
        tvGreeting.text = "$greeting $userName!"

        // Load profile picture if available
        if (!profilePicUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(profilePicUrl)
                .placeholder(R.drawable.default_profile_icon) // Fallback image
                .circleCrop()
                .into(imgProfile)
        }
>>>>>>> Stashed changes

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationUser)
        bottomNavigationView.setupWithNavController(navController)

        // Profile Click Handling
        imgProfile.setOnClickListener {
            val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
            Log.d("USER_ACTIVITY", "Profile clicked. Token: $isLoggedIn")

            if (!isLoggedIn) {
<<<<<<< Updated upstream
                Log.d("USER_ACTIVITY", "No token found. Redirecting to LoginActivity")
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else {
                Log.d("USER_ACTIVITY", "Token found. Navigating to ProfileFragment")
                navController.navigate(R.id.nav_profile)
=======
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                startActivity(Intent(this, UserProfileActivity::class.java))
>>>>>>> Stashed changes
            }
        }
    }

    private fun getGreeting(): String {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return when {
            hour in 5..11 -> "Good Morning"
            hour in 12..16 -> "Good Afternoon"
            hour in 17..20 -> "Good Evening"
            else -> "Good Night"
        }
    }
}
