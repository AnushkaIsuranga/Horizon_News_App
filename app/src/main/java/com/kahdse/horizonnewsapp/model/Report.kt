package com.kahdse.horizonnewsapp.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class Report(
    val title: String,
    val content: String,
    val category: String,
    val cover_photo: String, // Cloudinary image URL
    val status: String = "pending", // Default to 'pending' when submitted
    val createdAt: String
) {
    fun getFormattedDate(): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(createdAt)

            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            "Unknown Date"
        }
    }
}