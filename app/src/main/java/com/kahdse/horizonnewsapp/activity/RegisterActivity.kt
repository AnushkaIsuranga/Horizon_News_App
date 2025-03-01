package com.kahdse.horizonnewsapp.activity

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kahdse.horizonnewsapp.ApiService
import com.kahdse.horizonnewsapp.R
import com.kahdse.horizonnewsapp.model.UserResponse
import com.kahdse.horizonnewsapp.network.RetrofitClient
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class RegisterActivity : AppCompatActivity() {

    private lateinit var profilePicPreview: ImageView
    private lateinit var profilePicButton: Button
    private lateinit var firstNameInput: EditText
    private lateinit var lastNameInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var conf_passwordInput: EditText
    private lateinit var registerButton: Button

    private var selectedImageUri: Uri? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        profilePicPreview = findViewById(R.id.profilePicPreview)
        profilePicButton = findViewById(R.id.profilePicButton)
        firstNameInput = findViewById(R.id.firstNameInput)
        lastNameInput = findViewById(R.id.lastNameInput)
        phoneInput = findViewById(R.id.phoneInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        conf_passwordInput = findViewById(R.id.conf_passwordInput)
        registerButton = findViewById(R.id.registerButton)

        profilePicButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        registerButton.setOnClickListener {
            registerUser()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            profilePicPreview.setImageURI(selectedImageUri)
        }
    }

    private fun registerUser() {
        val firstName = firstNameInput.text.toString().trim()
        val lastName = lastNameInput.text.toString().trim()
        val phone = phoneInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        val phoneLong = phone.toLongOrNull()
        if (phoneLong == null) {
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show()
            return
        }

        if (passwordInput.text.toString().trim() != conf_passwordInput.text.toString().trim()) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // Show progress dialog
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Registering user...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        // Prepare text data
        val firstNamePart = firstName.toRequestBody("text/plain".toMediaTypeOrNull())
        val lastNamePart = lastName.toRequestBody("text/plain".toMediaTypeOrNull())
        val phonePart = phoneLong.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val emailPart = email.toRequestBody("text/plain".toMediaTypeOrNull())
        val passwordPart = password.toRequestBody("text/plain".toMediaTypeOrNull())
        val rolePart = "user".toRequestBody("text/plain".toMediaTypeOrNull())

        // Prepare image data if available
        var profilePicPart: MultipartBody.Part? = null
        selectedImageUri?.let { uri ->
            val filePath = getRealPathFromURI(uri)
            if (filePath != null) {
                val file = File(filePath)
                if (file.exists()) { // Ensure the file exists before sending
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    profilePicPart = MultipartBody.Part.createFormData("profile_pic", file.name, requestFile)
                } else {
                    Log.e("RegisterActivity", "File does not exist: $filePath")
                    progressDialog.dismiss()
                }
            } else {
                Log.e("RegisterActivity", "Failed to get real path from URI")
                progressDialog.dismiss()
            }
        }

        Log.d("RegisterActivity", "Selected Image URI: $selectedImageUri")

        val apiService: ApiService = RetrofitClient.createService(ApiService::class.java)
        val call: Call<UserResponse> = apiService.createUser(
            firstNamePart, lastNamePart, phonePart, emailPart, passwordPart, rolePart, profilePicPart
        )

        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                val responseBody = response.body()
                Log.d("RegisterActivity", "Response Code: ${response.code()}")
                Log.d("RegisterActivity", "Response Body: $responseBody")
                Log.d("RegisterActivity", "Error Body: ${response.errorBody()?.string()}")

                if (response.isSuccessful && responseBody != null) {
                    val imageUrl = responseBody.profile_pic
                    Log.d("RegisterActivity", "Profile Image URL: $imageUrl")

                    Toast.makeText(this@RegisterActivity, "Registration Successful", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("RegisterActivity", "Registration Failed: ${response.code()} - $errorBody")
                    Toast.makeText(this@RegisterActivity, "Registration Failed: ${response.code()}", Toast.LENGTH_LONG).show()
                    progressDialog.dismiss()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                progressDialog.dismiss()
                Log.e("RegisterActivity", "API Call Failed: ${t.localizedMessage}", t)
                Toast.makeText(this@RegisterActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                progressDialog.dismiss()
            }
        })
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            val path = cursor.getString(columnIndex)
            Log.d("RegisterActivity", "Real Image Path: $path")
            return path
        }
        return null
    }
}