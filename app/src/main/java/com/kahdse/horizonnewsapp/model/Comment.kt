package com.kahdse.horizonnewsapp.model

data class Comment(
    val id: String, // Unique ID for the comment
    val userId: String, // ID of the user who posted the comment
    val userName: String, // Name of the user who posted the comment
    val content: String, // The comment text
    val rating: Int, // The rating associated with the comment
    val timestamp: String // Timestamp of when the comment was posted
)