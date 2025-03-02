package com.kahdse.horizonnewsapp.model

import com.kahdse.horizonnewsapp.model.User

data class LoginResponse(
        val token: String,
        val user: User
)