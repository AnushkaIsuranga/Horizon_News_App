package com.kahdse.horizonnewsapp.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kahdse.horizonnewsapp.utils.ApiService
import com.kahdse.horizonnewsapp.utils.DBHelper
import com.kahdse.horizonnewsapp.R
import com.kahdse.horizonnewsapp.activity.ReporterActivity
import com.kahdse.horizonnewsapp.model.Draft
import com.kahdse.horizonnewsapp.network.RetrofitClient
import com.kahdse.horizonnewsapp.repository.DraftRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class CreateReportFragment : Fragment() {

    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText
    private lateinit var ivCoverPhoto: ImageView
    private lateinit var btnSubmit: View

    private var draftId: Int? = null
    private var selectedImageUri: Uri? = null
    private val apiService = RetrofitClient.retrofit.create(ApiService::class.java)
    private lateinit var draftRepository: DraftRepository
    private lateinit var database: DBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_create_report, container, false)

        // Initialize SQLite Database Helper
        database = DBHelper(requireContext())

        // Initialize DraftRepository (assuming it now uses SQLite)
        draftRepository = DraftRepository(requireContext())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etTitle = view.findViewById(R.id.etTitle)
        etContent = view.findViewById(R.id.etContent)
        ivCoverPhoto = view.findViewById(R.id.ivCoverPhoto)
        btnSubmit = view.findViewById(R.id.btnSubmit)

        ivCoverPhoto.setOnClickListener {
            openGallery()
        }

        btnSubmit.setOnClickListener {
            submitReport()
        }

        // Restore Draft if exists
        val draftId = arguments?.getInt("draftId", -1)
        val title = arguments?.getString("title", "")
        val content = arguments?.getString("content", "")
        val imageUri = arguments?.getString("imageUri", null)

        if (draftId != null && draftId != -1) {
            etTitle.setText(title)
            etContent.setText(content)
            imageUri?.let { ivCoverPhoto.setImageURI(Uri.parse(it)) }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            ivCoverPhoto.setImageURI(selectedImageUri)
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationReporter)?.visibility = View.GONE
        requireActivity().findViewById<LinearLayout>(R.id.topBar)?.visibility = View.GONE
        requireActivity().findViewById<FloatingActionButton>(R.id.btnCreateReport)?.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationReporter)?.visibility = View.VISIBLE
        requireActivity().findViewById<LinearLayout>(R.id.topBar)?.visibility = View.VISIBLE
        requireActivity().findViewById<FloatingActionButton>(R.id.btnCreateReport)?.visibility = View.VISIBLE

        if (draftId == null) {
            saveDraft()
        }
    }

    private fun saveDraft() {
        val title = etTitle.text.toString().trim()
        val content = etContent.text.toString().trim()
        val imageUriStr = selectedImageUri?.toString()

        if (title.isEmpty() && content.isEmpty() && imageUriStr == null) return

        val draft = Draft(
            id = draftId ?: 0,
            title = title,
            content = content,
            imageUri = imageUriStr,
            createdDate = System.currentTimeMillis(),
            lastAccessed = System.currentTimeMillis()
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (draftId == null) {
                    draftRepository.saveDraft(draft) // Save New Draft
                } else {
                    draftRepository.updateDraft(draft) // Update Existing Draft
                }
                Log.d("Draft", "Draft successfully saved!")
            } catch (e: Exception) {
                Log.e("Draft", "Error saving draft: ${e.message}")
            }
        }
    }

    private fun submitReport() {
        val title = etTitle.text.toString().trim()
        val content = etContent.text.toString().trim()

        if (title.isEmpty() || content.isEmpty() || selectedImageUri == null) {
            Toast.makeText(requireContext(), "All fields are required!", Toast.LENGTH_SHORT).show()
            return
        }

        // Retrieve token and role from SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Activity.MODE_PRIVATE)
        val token = sharedPreferences.getString("TOKEN", null)
        val role = sharedPreferences.getString("userRole", null)

        // Check if user is authenticated
        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Authentication required. Please log in.", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if user is a reporter
        if (role != "reporter") {
            Toast.makeText(requireContext(), "Access denied! Only reporters can submit reports.", Toast.LENGTH_SHORT).show()
            return
        }

        // Show progress dialog
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Submitting report...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val imageFile = selectedImageUri?.let { uriToFile(it) }
        if (imageFile == null) {
            Toast.makeText(requireContext(), "Image selection failed", Toast.LENGTH_SHORT).show()
            return
        }

        val titleBody = RequestBody.create("text/plain".toMediaTypeOrNull(), title)
        val contentBody = RequestBody.create("text/plain".toMediaTypeOrNull(), content)
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
        val imagePart = MultipartBody.Part.createFormData("cover_photo", imageFile.name, requestFile)

        // Use the authentication token in the API request
        apiService.createReport("Bearer $token", titleBody, contentBody, imagePart)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Report submitted successfully!", Toast.LENGTH_SHORT).show()

                        // Navigate back to ReporterActivity
                        val intent = Intent(requireContext(), ReporterActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        requireActivity().finish()
                    } else {
                        progressDialog.dismiss()
                        val errorBody = response.errorBody()?.string()
                        Log.e("CreateReportFragment", "Failed response: Code ${response.code()}, Body: $errorBody")
                        Toast.makeText(requireContext(), "Failed to submit report! Code: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("CreateReportFragment", "API Error", t)
                }
            })
    }

    private fun uriToFile(uri: Uri): File? {
        val contentResolver = requireContext().contentResolver
        val inputStream = contentResolver.openInputStream(uri) ?: return null
        val tempFile = File(requireContext().cacheDir, "upload_${System.currentTimeMillis()}.jpg")
        tempFile.outputStream().use { outputStream -> inputStream.copyTo(outputStream) }
        return tempFile
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1001
    }
}
