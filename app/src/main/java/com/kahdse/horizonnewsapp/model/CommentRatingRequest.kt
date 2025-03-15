package com.kahdse.horizonnewsapp.model

data class CommentRatingRequest(
    val rating: Int, // Rating value (1-5)
    val comment: String // The user's comment
)