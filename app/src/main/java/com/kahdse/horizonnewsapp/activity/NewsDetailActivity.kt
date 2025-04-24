package com.kahdse.horizonnewsapp.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kahdse.horizonnewsapp.R
import com.kahdse.horizonnewsapp.adapter.CommentsAdapter
import com.kahdse.horizonnewsapp.model.Comment
import com.kahdse.horizonnewsapp.model.CommentRatingRequest
import com.kahdse.horizonnewsapp.model.Report
import com.kahdse.horizonnewsapp.network.RetrofitClient
import com.kahdse.horizonnewsapp.utils.ApiService
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
    private lateinit var recyclerViewComments: RecyclerView
    private lateinit var commentsAdapter: CommentsAdapter
    private var newsId: String? = null

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
        recyclerViewComments = findViewById(R.id.recyclerViewComments)

        // Set up RecyclerView for comments
        commentsAdapter = CommentsAdapter()
        recyclerViewComments.layoutManager = LinearLayoutManager(this)
        recyclerViewComments.adapter = commentsAdapter

        // Retrieve the NEWS_ID from the intent
        newsId = intent.getStringExtra("NEWS_ID")

        if (newsId != null) {
            fetchNewsById(newsId!!) // Fetch news details using the ID
            fetchComments(newsId!!) // Fetch comments for the news item
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

                        // Set reporter name
                        val reporterName = if (report.reporter != null && report.reporter.first_name.isNotEmpty() && report.reporter.last_name.isNotEmpty()) {
                            "${report.reporter.first_name} ${report.reporter.last_name}"
                        } else {
                            "Anonymous Reporter"
                        }
                        reporterTextView.text = "Reporter: $reporterName"
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

    private fun fetchComments(newsId: String) {
        val apiService = RetrofitClient.createService(ApiService::class.java)
        apiService.getComments(newsId).enqueue(object : Callback<List<Comment>> {
            override fun onResponse(call: Call<List<Comment>>, response: Response<List<Comment>>) {
                if (response.isSuccessful) {
                    response.body()?.let { comments ->
                        commentsAdapter.submitList(comments)
                        findViewById<TextView>(R.id.newsDetailCommentsLabel).visibility = TextView.VISIBLE
                        recyclerViewComments.visibility = RecyclerView.VISIBLE
                    }
                } else {
                    Toast.makeText(this@NewsDetailActivity, "Failed to load comments", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Comment>>, t: Throwable) {
                Toast.makeText(this@NewsDetailActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun submitComment(newsId: String?, rating: Float, comment: String) {
        if (newsId == null) {
            Toast.makeText(this@NewsDetailActivity, "Invalid news ID", Toast.LENGTH_SHORT).show()
            return
        }

        val token = getTokenFromStorage()
        if (token.isEmpty()) {
            Toast.makeText(this@NewsDetailActivity, "Please log in to submit a comment", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            return
        }

        val apiService = RetrofitClient.createService(ApiService::class.java)
        val commentRequest = CommentRatingRequest(rating.toInt(), comment)
        val authToken = "Bearer $token"

        // Log the request details
        Log.d("NewsDetailActivity", "Submitting comment with token: $authToken")
        Log.d("NewsDetailActivity", "Request body: $commentRequest")

        apiService.addUserComment(newsId, authToken, commentRequest).enqueue(object : Callback<Comment> {
            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                if (response.isSuccessful) {
                    val newComment = response.body()
                    if (newComment != null) {
                        // Add the new comment to the RecyclerView
                        commentsAdapter.addComment(newComment)
                        Toast.makeText(this@NewsDetailActivity, "Comment submitted!", Toast.LENGTH_SHORT).show()
                        commentEditText.text.clear()
                        ratingBar.rating = 0f
                    }
                } else {
                    // Handle API error
                    val errorMessage = when (response.code()) {
                        400 -> "Rating and comment are required"
                        404 -> "Report not found"
                        401 -> "Unauthorized: Please log in again"
                        else -> "Failed to submit comment: ${response.code()}"
                    }
                    Toast.makeText(this@NewsDetailActivity, errorMessage, Toast.LENGTH_SHORT).show()

                    // Log the error response
                    Log.e("NewsDetailActivity", "API Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Comment>, t: Throwable) {
                Toast.makeText(this@NewsDetailActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("NewsDetailActivity", "Network Error: ${t.message}")
            }
        })
    }

    private fun getTokenFromStorage(): String {
        val sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("TOKEN", "") ?: ""
        Log.d("NewsDetailActivity", "Retrieved token: $token") // Debug log
        return token
    }
}