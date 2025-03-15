package com.kahdse.horizonnewsapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kahdse.horizonnewsapp.R
import com.kahdse.horizonnewsapp.model.Comment

class CommentsAdapter : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    private val comments = mutableListOf<Comment>()

    // Submit a new list of comments to the adapter
    fun submitList(newComments: List<Comment>) {
        comments.clear()
        comments.addAll(newComments)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.bind(comment)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userName: TextView = itemView.findViewById(R.id.commentUserName)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.commentRatingBar)
        private val content: TextView = itemView.findViewById(R.id.commentContent)
        private val timestamp: TextView = itemView.findViewById(R.id.commentTimestamp)

        fun bind(comment: Comment) {
            userName.text = comment.userName
            ratingBar.rating = comment.rating.toFloat()
            content.text = comment.content
            timestamp.text = comment.timestamp
        }
    }

    fun addComment(comment: Comment) {
        comments.add(comment)
        notifyItemInserted(comments.size - 1)
    }
}