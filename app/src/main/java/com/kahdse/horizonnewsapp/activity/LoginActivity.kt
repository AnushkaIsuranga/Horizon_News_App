package com.kahdse.horizonnewsapp.activity

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
            loginUser { success, token ->
                if (success) {
                    if (rememberMeCheckBox.isChecked && token != null) {
                        // Save token in SharedPreferences
                        sharedPref.edit().putString("auth_token", token).apply()
                    }

                    // Navigate to UserActivity after login
                    val intent = Intent(this, UserActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }
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

    private fun loginUser(callback: (Boolean, String?) -> Unit) {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            callback(false, null)
            return
        }

        val request = LoginRequest(email, password)
        val apiService = RetrofitClient.createService(ApiService::class.java)

        apiService.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    val user = loginResponse.user
                    val token = loginResponse.token
                    val role = user.role

                    // Save token in SharedPreferences if "Remember Me" is checked
                    if (rememberMeCheckBox.isChecked) {
                        sharedPref.edit().putString("TOKEN", token).apply()
                    }

                    Toast.makeText(applicationContext, "Welcome ${user.first_name}", Toast.LENGTH_SHORT).show()

                    // Navigate based on user role
                    val intent = when (role) {
                        "user" -> Intent(this@LoginActivity, UserActivity::class.java)
                        "reporter" -> Intent(this@LoginActivity, ReporterActivity::class.java)
                        else -> {
                            Toast.makeText(applicationContext, "Invalid role", Toast.LENGTH_SHORT).show()
                            callback(false, null)
                            return
                        }
                    }

                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                    // Call success callback with the correct token
                    callback(true, token)

                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Invalid credentials"
                        404 -> "User not found"
                        else -> "Login failed: ${response.code()}"
                    }
                    Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
                    callback(false, null) // Call failure callback
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                callback(false, null) // Call failure callback
            }
        })
    }
}
