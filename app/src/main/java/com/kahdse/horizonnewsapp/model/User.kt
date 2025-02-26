package com.kahdse.horizonnewsapp.model

data class User(
    val first_name: String,
    val last_name: String,
    val phone: Long,  // Use Long instead of Number
    val email: String?,
    val profile_pic: String?,
    val password: String,
    val role: String
)
