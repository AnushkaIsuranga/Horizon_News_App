package com.kahdse.horizonnewsapp.activity

import LoginResponse
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kahdse.horizonnewsapp.ApiService
import com.kahdse.horizonnewsapp.R
import com.kahdse.horizonnewsapp.model.LoginRequest
import com.kahdse.horizonnewsapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)

        loginButton.setOnClickListener {
            loginUser()
        }

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
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

        val request = LoginRequest(email, password)
        val apiService: ApiService = RetrofitClient.createService(ApiService::class.java)

        apiService.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()

                    if (loginResponse != null) {
                        val user = loginResponse.user
                        val token = loginResponse.token
                        val role = user.role // Extract role

                        // Save token in SharedPreferences
                        val sharedPref = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
                        sharedPref.edit().putString("TOKEN", token).apply()

                        Toast.makeText(applicationContext, "Welcome ${user.first_name}", Toast.LENGTH_SHORT).show()

                        // Open appropriate activity based on role
                        val intent = when (role) {
                            "user" -> Intent(this@LoginActivity, UserActivity::class.java)
                            "reporter" -> Intent(this@LoginActivity, ReporterActivity::class.java)
                            else -> {
                                Toast.makeText(applicationContext, "Invalid role", Toast.LENGTH_SHORT).show()
                                return
                            }
                        }
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        Toast.makeText(applicationContext, "Login failed: Invalid response", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "Login failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
