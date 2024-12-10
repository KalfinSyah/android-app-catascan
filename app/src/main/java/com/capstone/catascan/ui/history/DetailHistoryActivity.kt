package com.capstone.catascan.ui.history

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.capstone.catascan.R
import com.capstone.catascan.Utils.formatDateTime
import com.capstone.catascan.Utils.resultCustomStyling
import com.capstone.catascan.Utils.setFullScreen
import com.capstone.catascan.databinding.ActivityDetailHistoryBinding

class DetailHistoryActivity : AppCompatActivity() {
    private val binding: ActivityDetailHistoryBinding by lazy {
        ActivityDetailHistoryBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(binding.root)
        setFullScreen(window)
        setUpAction()
    }

    private fun setUpAction() {
        val historyTime = intent.getStringExtra(EXTRA_HISTORY_TIME)
        val historyImage = intent.getStringExtra(EXTRA_HISTORY_IMAGE)
        val historyPredictedClassName = intent.getStringExtra(EXTRA_HISTORY_PREDICTED_CLASS_NAME)
        val historyConfidence = intent.getStringExtra(EXTRA_HISTORY_CONFIDENCE)
        val resultBuff= when (historyPredictedClassName) {
            "immature cataract" -> ContextCompat.getString(this, R.string.info_immature)
            "mature cataract" -> ContextCompat.getString(this, R.string.info_mature)
            else -> ContextCompat.getString(this, R.string.infor_normal)
        }

        binding.textView.text = formatDateTime(historyTime.toString(), true)
        Glide.with(this)
            .load(historyImage)
            .into(binding.imageView)
        binding.textView2.text = resultCustomStyling(
            this,
            resultBuff,
            historyConfidence!!
        )
        binding.back.setOnClickListener {
            finish()
        }
    }

    companion object {
        const val EXTRA_HISTORY_TIME = "extra_history_time"
        const val EXTRA_HISTORY_IMAGE = "extra_history_image"
        const val EXTRA_HISTORY_PREDICTED_CLASS_NAME = "extra_history_predicted_class_name"
        const val EXTRA_HISTORY_CONFIDENCE = "extra_history_confidence"
    }
}