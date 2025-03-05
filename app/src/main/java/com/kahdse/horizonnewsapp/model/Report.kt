package com.kahdse.horizonnewsapp.model

import com.google.gson.annotations.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class Reporter(
    val _id: String,
    val first_name: String,
    val last_name: String
)

data class Report(
    @SerializedName("_id") val id: String?,
    val title: String,
    val content: String,
    val category: String,
    val cover_photo: String, // Cloudinary image URL
    val status: String = "pending", // Default to 'pending' when submitted
    val createdAt: String,
    val reporter: Reporter?
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