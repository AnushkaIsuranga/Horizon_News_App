package com.kahdse.horizonnewsapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.kahdse.horizonnewsapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kahdse.horizonnewsapp.fragment.CreateReportFragment

class ReporterActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reporter)

        // Set up Navigation Controller
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Set up Bottom Navigation with NavController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationReporter)
        bottomNavigationView.setupWithNavController(navController)

        val fabCreateReport = findViewById<FloatingActionButton>(R.id.btnCreateReport)
        fabCreateReport.setOnClickListener {
            val createReportFragment = CreateReportFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, createReportFragment)  // Replace container with new fragment
            transaction.addToBackStack(null)  // Allows back navigation
            transaction.commit()
        }
    }
}
