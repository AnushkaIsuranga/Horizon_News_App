package com.kahdse.horizonnewsapp

import LoginResponse
import com.kahdse.horizonnewsapp.model.LoginRequest
import com.kahdse.horizonnewsapp.model.User
import com.kahdse.horizonnewsapp.model.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
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

    @Multipart
    @POST("api/users/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}