package com.kahdse.horizonnewsapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kahdse.horizonnewsapp.R
import com.kahdse.horizonnewsapp.model.Draft
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DraftAdapter(
    private val onDraftClick: (Draft) -> Unit,
    private val onDeleteClick: (Draft) -> Unit
) : ListAdapter<Draft, DraftAdapter.DraftViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DraftViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_draft, parent, false)
        return DraftViewHolder(view)
    }

    override fun onBindViewHolder(holder: DraftViewHolder, position: Int) {
        val draft = getItem(position)
        holder.bind(draft)
    }

    fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    inner class DraftViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDraftTitle: TextView = itemView.findViewById(R.id.tvDraftTitle)
        private val tvCreatedDate: TextView = itemView.findViewById(R.id.tvCreatedDate)
        private val tvLastAccessed: TextView = itemView.findViewById(R.id.tvLastAccessed)
        private val btnDeleteDraft: Button = itemView.findViewById(R.id.btnDeleteDraft)

        fun bind(draft: Draft) {
            tvDraftTitle.text = draft.title
            tvCreatedDate.text = "Created: ${formatDateTime(draft.createdDate)}"
            tvLastAccessed.text = "Last Accessed: ${formatDateTime(draft.lastAccessed)}"

            itemView.setOnClickListener { onDraftClick(draft) }
            btnDeleteDraft.setOnClickListener { onDeleteClick(draft) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Draft>() {
            override fun areItemsTheSame(oldItem: Draft, newItem: Draft): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Draft, newItem: Draft): Boolean {
                return oldItem == newItem
            }
        }
    }
}
