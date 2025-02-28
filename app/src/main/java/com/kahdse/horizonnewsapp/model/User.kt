package com.kahdse.horizonnewsapp.model

data class User(
    val first_name: String,
    val last_name: String,
    val phone: String,
    val email: String,
    val profile_pic: String,
    val password: String,
    val role: String
)
