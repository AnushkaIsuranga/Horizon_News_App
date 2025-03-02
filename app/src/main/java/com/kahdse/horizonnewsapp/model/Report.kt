package com.kahdse.horizonnewsapp.model

data class Report(
    val title: String,
    val content: String,
    val cover_photo: String, // Cloudinary image URL
    val status: String = "pending" // Default to 'pending' when submitted
)