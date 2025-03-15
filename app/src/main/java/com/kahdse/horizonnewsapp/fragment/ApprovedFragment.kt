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
import com.kahdse.horizonnewsapp.R
import com.kahdse.horizonnewsapp.adapter.ApprovedAdapter
import com.kahdse.horizonnewsapp.adapter.PendingAdapter
import com.kahdse.horizonnewsapp.model.Report
import com.kahdse.horizonnewsapp.network.RetrofitClient
import com.kahdse.horizonnewsapp.utils.ApiService
import retrofit2.*

class ApprovedFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ApprovedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_approved, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewApproved)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchApprovedReports()

        return view
    }

    private fun fetchApprovedReports() {
        val apiService = RetrofitClient.createService(ApiService::class.java)

        apiService.getApprovedReports().enqueue(object : Callback<List<Report>> {
            override fun onResponse(call: Call<List<Report>>, response: Response<List<Report>>) {
                if (!isAdded) return

                Log.d("API_RESPONSE", "Response Code: ${response.code()}")
                Log.d("API_RESPONSE", "Raw Response: ${response.body().toString()}")

                if (response.isSuccessful) {
                    response.body()?.let { reports ->
                        Log.d("API_RESPONSE", "Parsed Reports: $reports")
                        adapter = ApprovedAdapter(reports)
                        recyclerView.adapter = adapter
                    }
                } else {
                    Log.e("API_RESPONSE", "Failed response: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "Failed to load approved reports", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Report>>, t: Throwable) {
                if (!isAdded) return

                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
