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
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
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
import kotlinx.coroutines.withContext
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
    private lateinit var draftRepository: DraftRepository
    private lateinit var database: DBHelper
    private lateinit var chipGroupCategories: ChipGroup

    private var draftId: Int? = null
    private var selectedImageUri: Uri? = null
    private val apiService = RetrofitClient.retrofit.create(ApiService::class.java)
    private var selectedCategory: String? = null
    private val categories = listOf(
        "Sports", "Technology", "Politics", "Gossip",
        "Weather", "Business", "Entertainment", "Lifestyle"
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_create_report, container, false)

        chipGroupCategories = view.findViewById(R.id.chipGroupCategories)
        setupCategoryChips()

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
        chipGroupCategories = view.findViewById(R.id.chipGroupCategories)
        ivCoverPhoto = view.findViewById(R.id.ivCoverPhoto)
        btnSubmit = view.findViewById(R.id.btnSubmit)

        ivCoverPhoto.setOnClickListener { openGallery() }
        btnSubmit.setOnClickListener { submitReport() }

        // Retrieve the draftId from the arguments
        draftId = arguments?.getInt("draftId", -1) ?: -1

        setupCategoryChips() // ✅ Load chips only once

        if (draftId != null && draftId != -1) {
            loadDraft(draftId!!) // ✅ Load draft properly
        }
    }

    private fun loadDraft(draftId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val draft = draftRepository.getDraftById(draftId)
            draft?.let {
                withContext(Dispatchers.Main) {
                    etTitle.setText(it.title)
                    etContent.setText(it.content)
                    selectedImageUri = it.imageUri?.toUri()
                    selectedCategory = it.category

                    // Set the image if the URI is not null
                    selectedImageUri?.let { uri ->
                        ivCoverPhoto.setImageURI(uri)
                    }

                    // Restore the selected category chip
                    restoreChipSelection(selectedCategory)
                }
            }
        }
    }

    private fun restoreChipSelection(selectedCategory: String?) {
        for (i in 0 until chipGroupCategories.childCount) {
            val chip = chipGroupCategories.getChildAt(i) as Chip
            chip.isChecked = chip.text.toString() == selectedCategory
        }
    }

    private fun setupCategoryChips() {
        chipGroupCategories.removeAllViews()
        for (category in categories) {
            val chip = Chip(requireContext()).apply {
                text = category
                isCheckable = true
                isChecked = category == selectedCategory // ✅ Ensures pre-selection works
                setOnClickListener {
                    selectedCategory = if (isChecked) category else null
                }
            }
            chipGroupCategories.addView(chip)
        }

        restoreChipSelection(selectedCategory) // ✅ Ensure correct chip is selected
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

        if (draftId != null && draftId != -1) {
            saveDraft() // ✅ Update the draft instead of creating a new one
        } else {
            saveNewDraft()
        }
    }


    private fun saveDraft() {
        val title = etTitle.text.toString().trim()
        val content = etContent.text.toString().trim()
        val imageUriStr = selectedImageUri?.toString()
        val category = selectedCategory

        if (title.isEmpty() && content.isEmpty() && imageUriStr == null && category.isNullOrEmpty()) return

        CoroutineScope(Dispatchers.IO).launch {
            draftId?.let { id ->
                val existingDraft = draftRepository.getDraftById(id)
                if (existingDraft != null) {
                    val draft = Draft(
                        id = id,
                        title = title,
                        content = content,
                        imageUri = imageUriStr,
                        category = category,
                        createdDate = existingDraft.createdDate,
                        lastAccessed = System.currentTimeMillis()
                    )

                    try {
                        draftRepository.updateDraft(draft)
                        Log.d("Draft", "Draft updated successfully!")
                    } catch (e: Exception) {
                        Log.e("Draft", "Error updating draft: ${e.message}")
                    }
                }
            } ?: run {
                saveNewDraft() // Only call this if draftId is null
            }
        }
    }

    private fun saveNewDraft() {
        val title = etTitle.text.toString().trim()
        val content = etContent.text.toString().trim()
        val imageUriStr = selectedImageUri?.toString()
        val category = selectedCategory

        if (title.isEmpty() && content.isEmpty() && imageUriStr == null && category.isNullOrEmpty()) return

        val draft = Draft(
            id = 0, // ✅ SQLite will auto-generate ID
            title = title,
            content = content,
            imageUri = imageUriStr,
            category = category,
            createdDate = System.currentTimeMillis(),
            lastAccessed = System.currentTimeMillis()
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val newId = draftRepository.saveDraft(draft).toInt() // ✅ Store generated ID
                draftId = newId // ✅ Assign to prevent duplicate drafts!
                Log.d("Draft", "New draft created with ID: $newId")
            } catch (e: Exception) {
                Log.e("Draft", "Error creating draft: ${e.message}")
            }
        }
    }

    private fun submitReport() {
        val title = etTitle.text.toString().trim()
        val content = etContent.text.toString().trim()
        val category = selectedCategory

        if (title.isEmpty() || content.isEmpty() || selectedImageUri == null || category.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "All fields, including category, are required!", Toast.LENGTH_SHORT).show()
            return
        }

        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Activity.MODE_PRIVATE)
        val token = sharedPreferences.getString("TOKEN", null)
        val role = sharedPreferences.getString("userRole", null)

        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Authentication required. Please log in.", Toast.LENGTH_SHORT).show()
            return
        }

        if (role != "reporter") {
            Toast.makeText(requireContext(), "Access denied! Only reporters can submit reports.", Toast.LENGTH_SHORT).show()
            return
        }

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
        val categoryBody = RequestBody.create("text/plain".toMediaTypeOrNull(), category)
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
        val imagePart = MultipartBody.Part.createFormData("cover_photo", imageFile.name, requestFile)

        apiService.createReport("Bearer $token", titleBody, contentBody, categoryBody, imagePart)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    progressDialog.dismiss()
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Report submitted successfully!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireContext(), ReporterActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        requireActivity().finish()
                    } else {
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
