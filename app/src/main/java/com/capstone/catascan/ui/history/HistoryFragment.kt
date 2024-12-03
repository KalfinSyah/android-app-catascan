package com.capstone.catascan.ui.history

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.catascan.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: HistoryAdapter
    private val viewModel: HistoryViewModel by viewModels {
        HistoryViewModelFactory.getInstance(activity?.application as Application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val view = binding.root

        // code here
        viewModel.getAllHistory().observe(viewLifecycleOwner) {
            binding.textView6.visibility =  if (it.isNullOrEmpty()) View.VISIBLE else View.GONE
            adapter.setListHistory(it)
        }
        adapter = HistoryAdapter()
        binding.rvHistory .layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}