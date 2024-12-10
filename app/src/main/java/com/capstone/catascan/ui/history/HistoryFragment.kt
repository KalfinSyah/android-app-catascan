package com.capstone.catascan.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.capstone.catascan.data.history.History
import com.capstone.catascan.data.pref.UserPreference
import com.capstone.catascan.data.pref.dataStore
import com.capstone.catascan.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: HistoryAdapter
    private var listHistory: MutableList<History> = mutableListOf()


    private val userPreference: UserPreference by lazy {
        UserPreference.getInstance(requireContext().dataStore)
    }
    private val viewModel: HistoryViewModel by viewModels {
        HistoryViewModelFactory(userPreference)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val view = binding.root


        // code here
        adapter = HistoryAdapter()
        binding.rvHistory.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvHistory.adapter = adapter

        // get all online history
        viewModel.getUserSession().observe(viewLifecycleOwner) {
            viewModel.getAllCloudHistory(it.token)
        }

        viewModel.historyItem.observe(viewLifecycleOwner) {
            binding.textView6.visibility = if (it.isNullOrEmpty()) View.VISIBLE else View.GONE
            if (it != null) {
                listHistory.clear() // Clear the list to avoid duplicates
                for (i in it) {
                    addHistoryItem(i.imageUrl, i.result)
                }
                adapter.setListHistory(listHistory)
            }
        }
        return view
    }

    private fun addHistoryItem(imageUrl: String, result: String) {
        val historyItem = History(image = imageUrl, result = result)
        listHistory.add(historyItem)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}