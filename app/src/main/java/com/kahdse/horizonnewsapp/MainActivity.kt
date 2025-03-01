package com.kahdse.horizonnewsapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kahdse.horizonnewsapp.activity.LoginActivity
import com.kahdse.horizonnewsapp.activity.ReporterActivity
import com.kahdse.horizonnewsapp.activity.UserActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val intent = Intent(this, UserActivity::class.java)
        startActivity(intent)

        finish()
    }
}