package com.kahdse.horizonnewsapp.activity

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.kahdse.horizonnewsapp.R
import com.kahdse.horizonnewsapp.model.CommentRatingRequest
import com.kahdse.horizonnewsapp.model.Report
import com.kahdse.horizonnewsapp.network.RetrofitClient
import com.kahdse.horizonnewsapp.utils.ApiService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsDetailActivity : AppCompatActivity() {

    private lateinit var titleTextView: TextView
    private lateinit var contentTextView: TextView
    private lateinit var coverImageView: ImageView
    private lateinit var reporterTextView: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var commentEditText: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)

        // Initialize views
        titleTextView = findViewById(R.id.newsDetailTitle)
        contentTextView = findViewById(R.id.newsDetailContent)
        coverImageView = findViewById(R.id.newsDetailImage)
        reporterTextView = findViewById(R.id.newsDetailReporter)
        ratingBar = findViewById(R.id.newsDetailRatingBar)
        commentEditText = findViewById(R.id.newsDetailCommentEditText)
        submitButton = findViewById(R.id.newsDetailSubmitButton)

        // Retrieve the NEWS_ID from the intent
        val newsId = intent.getStringExtra("NEWS_ID")

        if (newsId != null) {
            fetchNewsById(newsId) // Fetch news details using the ID
        } else {
            Toast.makeText(this, "News not found", Toast.LENGTH_SHORT).show()
            finish() // Close the activity if no ID is provided
        }

        // Set up the comment submission button
        submitButton.setOnClickListener {
            val rating = ratingBar.rating
            val comment = commentEditText.text.toString()

            if (rating == 0f || comment.isEmpty()) {
                Toast.makeText(this, "Please provide a rating and a comment", Toast.LENGTH_SHORT).show()
            } else {
                submitComment(newsId, rating, comment)
            }
        }
    }

    private fun fetchNewsById(id: String) {
        val apiService = RetrofitClient.createService(ApiService::class.java)
        apiService.getReportById(id).enqueue(object : Callback<Report> {
            override fun onResponse(call: Call<Report>, response: Response<Report>) {
                if (response.isSuccessful) {
                    response.body()?.let { report ->
                        titleTextView.text = report.title
                        contentTextView.text = report.content

                        Glide.with(this@NewsDetailActivity)
                            .load(report.cover_photo)
                            .into(coverImageView)

                        Log.d("API_RESPONSE", "Reporter Object: ${report.reporter}")

                        // Set reporter name
                        val reporterName = if (report.reporter != null && report.reporter.first_name.isNotEmpty() && report.reporter.last_name.isNotEmpty()) {
                            "${report.reporter.first_name} ${report.reporter.last_name}"
                        } else {
                            "Anonymous Reporter"
                        }
                        reporterTextView.text = "Reporter: $reporterName"
                        Log.d("API_RESPONSE", "Parsed Report: $report")

                    }
                } else {
                    // Handle API error
                    Toast.makeText(this@NewsDetailActivity, "Failed to load news", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onFailure(call: Call<Report>, t: Throwable) {
                // Handle network failure
                Toast.makeText(this@NewsDetailActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    private fun submitComment(newsId: String?, rating: Float, comment: String) {
        if (newsId == null) {
            Toast.makeText(this@NewsDetailActivity, "Invalid news ID", Toast.LENGTH_SHORT).show()
            return
        }

        val apiService = RetrofitClient.createService(ApiService::class.java)
        val commentRequest = CommentRatingRequest(rating.toInt(), comment)
        val token = "Bearer " + getTokenFromStorage()

        apiService.addUserComment(newsId, token, commentRequest).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@NewsDetailActivity, "Comment submitted!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@NewsDetailActivity, "Failed to submit comment", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@NewsDetailActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getTokenFromStorage(): String {
        val sharedPreferences = this.getSharedPreferences("AppPreferences", MODE_PRIVATE)
        return sharedPreferences.getString("auth_token", "") ?: ""
    }
}
