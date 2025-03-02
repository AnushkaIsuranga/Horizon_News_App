package com.kahdse.horizonnewsapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.kahdse.horizonnewsapp.AppDatabase
import com.kahdse.horizonnewsapp.DraftRepository
import com.kahdse.horizonnewsapp.R
import com.kahdse.horizonnewsapp.adapter.DraftAdapter
import com.kahdse.horizonnewsapp.model.Draft
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DraftFragment : Fragment() {

    private lateinit var draftAdapter: DraftAdapter
    private lateinit var draftRepository: DraftRepository
    private lateinit var database: AppDatabase
    private lateinit var rvDrafts: RecyclerView
    private lateinit var tvNoDrafts: TextView
    private var draftList: MutableList<Draft> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_draft, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvDrafts = view.findViewById(R.id.rvDrafts)
        tvNoDrafts = view.findViewById(R.id.tvNoDrafts)

        database = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "draft_database"
        ).build()

        draftAdapter = DraftAdapter(
            onDraftClick = { draft ->
                openDraft(draft)
            },
            onDeleteClick = { draft ->
                deleteDraft(draft)
            }
        )

        rvDrafts.layoutManager = LinearLayoutManager(requireContext())
        rvDrafts.adapter = draftAdapter

        loadDrafts()
    }

    private fun loadDrafts() {
        lifecycleScope.launch {
            val drafts = withContext(Dispatchers.IO) {draftRepository.getAllDrafts()}
            draftAdapter.submitList(drafts)
        }
    }

    private fun openDraft(draft: Draft) {
        val bundle = Bundle().apply {
            putInt("draftId", draft.id)
            putString("title", draft.title)
            putString("content", draft.content)
            putString("imageUri", draft.imageUri)
        }
        findNavController().navigate(R.id.action_draftFragment_to_createReportFragment, bundle)
    }

    private fun deleteDraft(draft: Draft) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) { draftRepository.deleteDraft(draft)}
            loadDrafts()
            Toast.makeText(requireContext(), "Draft deleted", Toast.LENGTH_SHORT).show()
        }
    }
}
