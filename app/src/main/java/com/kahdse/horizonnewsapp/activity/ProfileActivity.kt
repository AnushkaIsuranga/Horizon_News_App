import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.kahdse.horizonnewsapp.R
import com.kahdse.horizonnewsapp.activity.LoginActivity

class ProfileActivity : AppCompatActivity() {
    private lateinit var profileImageView: ImageView
    private lateinit var firstNameTextView: TextView
    private lateinit var lastNameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var btnLogout: Button
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

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
}
