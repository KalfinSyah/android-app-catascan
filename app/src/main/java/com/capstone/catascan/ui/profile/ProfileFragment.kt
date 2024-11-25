package com.capstone.catascan.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.capstone.catascan.R
import com.capstone.catascan.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        // code here

        Glide.with(requireContext())
            .load("https://picsum.photos/200")
            .into(binding.profile)
        binding.name.text = getString(R.string.say_hi_to, "Testing Nama Panjang")
        binding.email.text = getString(R.string.email, "testingnamapanja@gmail.com")

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}