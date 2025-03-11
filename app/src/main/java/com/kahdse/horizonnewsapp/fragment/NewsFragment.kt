package com.kahdse.horizonnewsapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kahdse.horizonnewsapp.R
import com.kahdse.horizonnewsapp.adapter.NewsAdapter
import com.kahdse.horizonnewsapp.model.Report
import com.kahdse.horizonnewsapp.network.RetrofitClient
import com.kahdse.horizonnewsapp.utils.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NewsAdapter
    private var newsId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newsId = arguments?.getString("news_id")  // <-- Get the ID from arguments
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_news, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewHomeNews)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchApprovedNews()

        return view
    }

    private fun fetchApprovedNews() {
        val apiService = RetrofitClient.createService(ApiService::class.java)

        // Use the newsId to fetch specific news details
        if (newsId != null) {
            apiService.getReportById(newsId!!).enqueue(object : Callback<Report> {
                override fun onResponse(call: Call<Report>, response: Response<Report>) {
                    if (!isAdded || isDetached) return

                    Log.d("API_RESPONSE", "Response Code: ${response.code()}")
                    Log.d("API_RESPONSE", "Raw Response: ${response.body().toString()}")

                    if (response.isSuccessful) {
                        response.body()?.let { report ->
                            Log.d("API_RESPONSE", "Parsed Report: $report")
                            // Update the adapter with a single report
                            adapter = NewsAdapter(listOf(report))
                            recyclerView.adapter = adapter
                        }
                    } else {
                        Log.e("API_RESPONSE", "Failed response: ${response.errorBody()?.string()}")
                        Toast.makeText(requireContext(), "Failed to load news", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Report>, t: Throwable) {
                    if (!isAdded || isDetached) return
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            // If newsId is null, fetch all approved news
            apiService.getApprovedReports().enqueue(object : Callback<List<Report>> {
                override fun onResponse(call: Call<List<Report>>, response: Response<List<Report>>) {
                    if (!isAdded || isDetached) return

                    Log.d("API_RESPONSE", "Response Code: ${response.code()}")
                    Log.d("API_RESPONSE", "Raw Response: ${response.body().toString()}")

                    if (response.isSuccessful) {
                        response.body()?.let { reports ->
                            Log.d("API_RESPONSE", "Parsed Reports: $reports")
                            adapter = NewsAdapter(reports)
                            recyclerView.adapter = adapter
                        }
                    } else {
                        Log.e("API_RESPONSE", "Failed response: ${response.errorBody()?.string()}")
                        Toast.makeText(requireContext(), "Failed to load news", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Report>>, t: Throwable) {
                    if (!isAdded || isDetached) return
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}