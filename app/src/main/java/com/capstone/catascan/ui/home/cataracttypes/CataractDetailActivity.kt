package com.capstone.catascan.ui.home.cataracttypes

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.capstone.catascan.R
import com.capstone.catascan.Utils.setFullScreen
import com.capstone.catascan.data.CataractType

class CataractDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_cataract_detail)
        setFullScreen(window)


        val cataractType = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("cataract_type", CataractType::class.java)
        }else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("cataract_type")
        }

        val nameTextView: TextView = findViewById(R.id.tv_cataract_name)
        val descriptionTextView: TextView = findViewById(R.id.tv_cataract_description)
        val photoImageView: ImageView = findViewById(R.id.img_cataract_detail)
        val backButton: Button = findViewById(R.id.btn_back)

        cataractType?.let {
            nameTextView.text = it.name
            descriptionTextView.text = it.description
            photoImageView.setImageResource(it.photo)
        }

        backButton.setOnClickListener {
            finish()
        }
    }
}