package com.kahdse.horizonnewsapp.adapter

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kahdse.horizonnewsapp.R
import com.kahdse.horizonnewsapp.model.Report
import java.util.*
import java.util.Locale

class ReportAdapter(private var reports: List<Report>) :
    RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val date: TextView = itemView.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_report, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reports[position]
        holder.title.text = report.title
        holder.date.text = report.getFormattedDate()
    }

    override fun getItemCount(): Int = reports.size

    fun updateData(newReports: List<Report>) {
        reports = newReports
        notifyDataSetChanged()
    }
}
