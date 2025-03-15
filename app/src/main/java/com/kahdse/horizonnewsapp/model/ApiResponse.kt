package com.kahdse.horizonnewsapp.model

data class ApiResponse(
    val success: Boolean,
    val reports: List<Report>
)