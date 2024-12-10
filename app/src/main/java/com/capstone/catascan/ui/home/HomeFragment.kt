package com.capstone.catascan.ui.home

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.capstone.catascan.R
import com.capstone.catascan.Utils.setBtnRetryAndErrorMsg
import com.capstone.catascan.Utils.setLoading
import com.capstone.catascan.data.CataractType
import com.capstone.catascan.data.pref.UserPreference
import com.capstone.catascan.data.pref.dataStore
import com.capstone.catascan.data.response.ArticlesItem
import com.capstone.catascan.databinding.FragmentHomeBinding
import com.capstone.catascan.ui.home.cataractprevention.DetailPreventActivity
import com.capstone.catascan.ui.home.cataracttypes.CataractAdapter
import com.capstone.catascan.ui.home.relatednews.NewsAdapter

class HomeFragment : Fragment() {

    // binding
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // user preference
    private val userPreference: UserPreference by lazy {
        UserPreference.getInstance(requireContext().dataStore)
    }

    // viewmodel
    private val viewModel by viewModels<HomeViewModel> {
        HomeViewModelFactory(userPreference, activity?.application as Application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        setCataractTypes()
        setData()
        setAction()

        return view
    }

    private fun setData() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            setLoading(binding.progressBar, it)
        }

        viewModel.articlesItem.observe(viewLifecycleOwner) {
            setNews(it)
        }

        viewModel.errMsg.observe(viewLifecycleOwner) {
            setBtnRetryAndErrorMsg(binding.retryButton, binding.errorMsg, it)
        }

        viewModel.getUserSession().observe(viewLifecycleOwner) {
            viewModel.getUserData(it.token)
        }

        viewModel.getUserResult.observe(viewLifecycleOwner) {
            binding.username.text = it.name
            Glide.with(requireContext())
                .load( it?.profileImageUrl ?: "https://picsum.photos/200")
                .into(binding.profileImage)
        }
    }

    private fun setAction() {
        binding.scanCard.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_scanFragment)
        }
        binding.retryButton.setOnClickListener {
            viewModel.retry()
        }
        binding.tipsCard.setOnClickListener {
            requireContext().startActivity(Intent(requireContext(), DetailPreventActivity::class.java))
        }
    }

    private fun setCataractTypes() {
        val dataName = resources.getStringArray(R.array.data_name)
        val dataDescription = resources.getStringArray(R.array.data_description)
        val dataPhoto = resources.obtainTypedArray(R.array.data_photo)
        val cataractList = mutableListOf<CataractType>()
        for (i in dataName.indices) {
            val name = dataName[i]
            val description = dataDescription[i]
            val photo = dataPhoto.getResourceId(i, -1)
            cataractList.add(CataractType(name, description, photo))
        }

        dataPhoto.recycle()
        binding.rvtypes.adapter = CataractAdapter(cataractList)
    }

    private fun setNews(news: List<ArticlesItem?>?) {
        // Filter news to remove items with "[Removed]" in the title or description
        // and include only items with "cataract" in the description
        val filteredNews = news?.filter { article ->
            val titleDoesNotContainRemoved = article?.title?.contains("[Removed]", ignoreCase = true) == false
            val descriptionDoesNotContainRemoved = article?.description?.contains("[Removed]", ignoreCase = true) == false
            titleDoesNotContainRemoved && descriptionDoesNotContainRemoved
        }

        val adapter = NewsAdapter()
        adapter.submitList(filteredNews)
        binding.recyclerViewnews.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}