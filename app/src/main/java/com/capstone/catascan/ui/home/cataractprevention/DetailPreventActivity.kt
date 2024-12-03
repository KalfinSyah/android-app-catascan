package com.capstone.catascan.ui.home.cataractprevention

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.catascan.Utils.setFullScreen
import com.capstone.catascan.databinding.ActivityDetailPreventBinding

class DetailPreventActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityDetailPreventBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(binding.root)
        setFullScreen(window)

        binding.backButton.setOnClickListener {
            finish()
        }
    }
}