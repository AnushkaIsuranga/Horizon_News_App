package com.kahdse.horizonnewsapp.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kahdse.horizonnewsapp.R
import com.kahdse.horizonnewsapp.adapter.ReportAdapter
import com.kahdse.horizonnewsapp.model.ApiResponse
import com.kahdse.horizonnewsapp.model.Report
import com.kahdse.horizonnewsapp.network.RetrofitClient
import com.kahdse.horizonnewsapp.utils.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFilterFragment : Fragment() {

    private lateinit var etSearch: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var reportAdapter: ReportAdapter

    private var selectedCategory: String? = null
    private var apiService = RetrofitClient.retrofit.create(ApiService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etSearch = view.findViewById(R.id.etSearch)
        spinnerCategory = view.findViewById(R.id.spinnerCategory)
        recyclerView = view.findViewById(R.id.recyclerViewReports)

        setupRecyclerView()
        setupCategorySpinner()
        setupSearchListener()
    }

    private fun setupRecyclerView() {
        reportAdapter = ReportAdapter(emptyList())
        recyclerView.adapter = reportAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupCategorySpinner() {
        val categories = listOf("All", "Sports", "Technology", "Politics", "Gossip",
            "Weather", "Business", "Entertainment", "Lifestyle")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategory = if (position == 0) null else categories[position]
                searchReports(etSearch.text.toString(), selectedCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupSearchListener() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchReports(s.toString(), selectedCategory)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun searchReports(query: String, category: String?) {
        apiService.searchReports(query, category).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.success == true) {
                        val reports = apiResponse.reports ?: emptyList()
                        reportAdapter.updateData(reports)
                    } else {
                        Toast.makeText(requireContext(), "Search failed!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Search failed!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}