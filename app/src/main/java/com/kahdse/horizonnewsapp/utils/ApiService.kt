package com.kahdse.horizonnewsapp.utils

import com.kahdse.horizonnewsapp.model.LoginRequest
import com.kahdse.horizonnewsapp.model.LoginResponse
import com.kahdse.horizonnewsapp.model.Report
import com.kahdse.horizonnewsapp.model.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("api/users/register")
    fun createUser(
        @Part("first_name") firstName: RequestBody,
        @Part("last_name") lastName: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("role") role: RequestBody,
        @Part profile_pic: MultipartBody.Part?
    ): Call<UserResponse>

    @POST("api/users/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>


    @Multipart
    @POST("api/news/create")
    fun createReport(
        @Header("Authorization") authToken: String,  // Add this line
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part coverPhoto: MultipartBody.Part
    ): Call<Void>

    @GET("api/news/approved")
    fun getApprovedReports(): Call<List<Report>>

    @GET("api/news/pending")
    fun getPendingReports(): Call<List<Report>>
}