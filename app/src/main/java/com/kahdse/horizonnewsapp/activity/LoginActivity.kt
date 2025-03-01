package com.kahdse.horizonnewsapp.activity

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kahdse.horizonnewsapp.ApiService
import com.kahdse.horizonnewsapp.R
import com.kahdse.horizonnewsapp.model.LoginRequest
import com.kahdse.horizonnewsapp.model.LoginResponse
import com.kahdse.horizonnewsapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var rememberMeCheckBox: CheckBox
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var skipLoginButton: Button
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize SharedPreferences
        sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)

        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)
        skipLoginButton = findViewById(R.id.skipLoginButton)

        loginButton.setOnClickListener {
            loginUser()

        }

        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        skipLoginButton.setOnClickListener {
            // Navigate to UserActivity without login
            val intent = Intent(this, UserActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun loginUser() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        // Show progress dialog
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Registering user...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val request = LoginRequest(email, password)
        val apiService = RetrofitClient.createService(ApiService::class.java)

        apiService.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    val user = loginResponse.user
                    val token = loginResponse.token
                    val role = user.role

                    Toast.makeText(applicationContext, "Welcome ${user.first_name}", Toast.LENGTH_SHORT).show()

                    // Save token ONLY if "Remember Me" is checked
                    if (rememberMeCheckBox.isChecked) {
                        sharedPref.edit().putString("TOKEN", token).apply()
                        saveLoginState(email, role)
                    } else {
                        // Remove any previously saved token
                        sharedPref.edit().remove("TOKEN").apply()
                    }

                    // Navigate based on user role
                    val intent = when (role) {
                        "user" -> Intent(this@LoginActivity, UserActivity::class.java)
                        "reporter" -> Intent(this@LoginActivity, ReporterActivity::class.java)
                        else -> {
                            progressDialog.dismiss()
                            Toast.makeText(applicationContext, "Invalid role", Toast.LENGTH_SHORT).show()
                            return
                        }
                    }
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                } else {
                    progressDialog.dismiss()
                    val errorMessage = when (response.code()) {
                        400 -> "Invalid credentials"
                        404 -> "User not found"
                        else -> "Login failed: ${response.code()}"
                    }
                    Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Save user login state and role
    private fun saveLoginState(email: String, role: String) {
        with(sharedPref.edit()) {
            putBoolean("isLoggedIn", true)
            putString("userEmail", email)
            putString("userRole", role)
            apply()
        }
    }
}
