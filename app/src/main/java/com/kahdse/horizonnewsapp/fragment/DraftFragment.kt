package com.kahdse.horizonnewsapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kahdse.horizonnewsapp.utils.DBHelper
import com.kahdse.horizonnewsapp.R
import com.kahdse.horizonnewsapp.adapter.DraftAdapter
import com.kahdse.horizonnewsapp.model.Draft
import com.kahdse.horizonnewsapp.repository.DraftRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DraftFragment : Fragment() {

    private lateinit var draftAdapter: DraftAdapter
    private lateinit var draftRepository: DraftRepository
    private lateinit var databaseHelper: DBHelper
    private lateinit var rvDrafts: RecyclerView
    private lateinit var tvNoDrafts: TextView

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

        databaseHelper = DBHelper(requireContext())
        draftRepository = DraftRepository(requireContext())

        draftAdapter = DraftAdapter(
            onDraftClick = { draft -> openDraft(draft) },
            onDeleteClick = { draft -> deleteDraft(draft.id) }
        )

        rvDrafts.layoutManager = LinearLayoutManager(requireContext())
        rvDrafts.adapter = draftAdapter

        loadDrafts()

        // Observe changes when returning from CreateReportFragment
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("refreshDrafts")
            ?.observe(viewLifecycleOwner) { shouldRefresh ->
                if (shouldRefresh) {
                    Log.d("DraftFragment", "Refreshing drafts after save")
                    loadDrafts()
                }
            }
    }

    private fun loadDrafts() {
        lifecycleScope.launch(Dispatchers.IO) {
            val drafts = draftRepository.getAllDrafts()

            withContext(Dispatchers.Main) {
                draftAdapter.submitList(drafts)
                draftAdapter.notifyDataSetChanged()
                tvNoDrafts.visibility = if (drafts.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }


    private fun openDraft(draft: Draft) {
        val bundle = Bundle().apply {
            putInt("draftId", draft.id)
        }
        findNavController().navigate(R.id.action_draftFragment_to_createReportFragment, bundle)
    }

    private fun deleteDraft(draftId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            draftRepository.deleteDraft(draftId)
            withContext(Dispatchers.Main) {
                loadDrafts()
                Toast.makeText(requireContext(), "Draft deleted", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
