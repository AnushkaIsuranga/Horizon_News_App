package com.kahdse.horizonnewsapp.fragment;

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.kahdse.horizonnewsapp.R
import com.kahdse.horizonnewsapp.adapter.PendingAdapter
import com.kahdse.horizonnewsapp.model.Report
import com.kahdse.horizonnewsapp.network.RetrofitClient
import com.kahdse.horizonnewsapp.utils.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PendingFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PendingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pending, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewPending)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchPendingReports()

        return view
    }

    private fun fetchPendingReports() {
        val apiService = RetrofitClient.createService(ApiService::class.java)

        apiService.getPendingReports().enqueue(object : Callback<List<Report>> {
            override fun onResponse(call: Call<List<Report>>, response: Response<List<Report>>) {
                if (!isAdded) return

                Log.d("API_RESPONSE", "Response Code: ${response.code()}")
                Log.d("API_RESPONSE", "Raw Response: ${response.body().toString()}")

                if (response.isSuccessful) {
                    response.body()?.let { reports ->
                        Log.d("API_RESPONSE", "Parsed Reports: $reports")
                        adapter = PendingAdapter(reports)
                        recyclerView.adapter = adapter
                    }
                } else {
                    Log.e("API_RESPONSE", "Failed response: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "Failed to load pending reports", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Report>>, t: Throwable) {
                if (!isAdded) return

                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
