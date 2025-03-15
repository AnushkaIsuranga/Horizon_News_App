package com.kahdse.horizonnewsapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kahdse.horizonnewsapp.R
import com.kahdse.horizonnewsapp.model.Report
import com.kahdse.horizonnewsapp.activity.NewsDetailActivity

class NewsAdapter(private val newsList: List<Report>) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val newsItem = newsList[position]
        holder.bind(newsItem)
    }

    override fun getItemCount(): Int = newsList.size

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageNewsCover: ImageView = itemView.findViewById(R.id.imageNewsCover)
        private val textNewsTitle: TextView = itemView.findViewById(R.id.textNewsTitle)
        private val textNewsContent: TextView = itemView.findViewById(R.id.textNewsContent)

        fun bind(news: Report) {
            textNewsTitle.text = news.title
            textNewsContent.text = news.content

            // Load image using Glide with placeholder and error handling
            Glide.with(itemView.context)
                .load(news.cover_photo)
                .into(imageNewsCover)

            // Handle item click
            itemView.setOnClickListener {
                if (news.id.isNullOrEmpty()) {
                    Toast.makeText(itemView.context, "News unavailable", Toast.LENGTH_SHORT).show()
                } else {
                    // Launch NewsDetailActivity with the news ID
                    val context = itemView.context
                    val intent = Intent(context, NewsDetailActivity::class.java).apply {
                        putExtra("NEWS_ID", news.id)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }
}