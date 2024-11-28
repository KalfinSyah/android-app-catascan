package com.capstone.catascan.ui.scan.preview

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.capstone.catascan.R
import com.capstone.catascan.Utils
import com.capstone.catascan.Utils.getBitmapFromUri
import com.capstone.catascan.databinding.ActivityPreviewBinding
import com.capstone.catascan.ml.CataractModel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class PreviewActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityPreviewBinding.inflate(layoutInflater)
    }

    private val viewModel: PreviewViewModel by viewModels {
        PreviewViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()

        @Suppress("DEPRECATION")
        window.apply {
            statusBarColor = android.graphics.Color.TRANSPARENT
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }

        viewModel.fromRecent.value = intent.getStringExtra(EXTRA_RESULT)

        viewModel.fromRecent.observe(this) {
            if (!it.isNullOrBlank()) {
                binding.result.visibility = View.VISIBLE
                binding.scanBtn.visibility = View.GONE
                binding.textView.text = getString(R.string.preview_or_result, "Result")
                binding.result.text =  Utils.resultCustomStyling(this, intent.getStringExtra(EXTRA_RESULT) ?: "")
            }
        }

        viewModel.setResultVisibility.observe(this) {
            if (it == true) {
                binding.result.visibility = View.VISIBLE
                binding.scanBtn.visibility = View.GONE
                binding.textView.text = getString(R.string.preview_or_result, "Result")
            } else {
                binding.result.visibility = View.GONE
                binding.scanBtn.visibility = View.VISIBLE
                binding.textView.text = getString(R.string.preview_or_result, "Preview")
            }
        }

        viewModel.setCapturedImage.observe(this) {
            Glide.with(this)
                .load(it)
                .into(binding.capturedImage)
        }
        viewModel.setCapturedImage.value = intent.getStringExtra(EXTRA_CAPTURED_IMAGE)?.toUri()
        viewModel.result.observe(this) {
            binding.result.text = it
        }

        binding.textView.text = getString(R.string.preview_or_result, "Preview")
        binding.imageButton.setOnClickListener {
            finish()
        }
        binding.scanBtn.setOnClickListener {
            viewModel.setResultVisibility.value = true
            classifyImage(viewModel.setCapturedImage.value ?: Uri.EMPTY)
        }
    }

    private fun classifyImage(imageUri: Uri) {
        val bitmap = getBitmapFromUri(contentResolver, imageUri)

        // Ensure the bitmap is in ARGB_8888 format
        val argbBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        // Load the model
        val model = CataractModel.newInstance(this)

        // Preprocess the input image
        val tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(argbBitmap)

        // Resize and normalize image to match model requirements
        val imageProcessor = org.tensorflow.lite.support.image.ImageProcessor.Builder()
            .add(org.tensorflow.lite.support.image.ops.ResizeOp(224, 224, org.tensorflow.lite.support.image.ops.ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(0.0f, 255.0f)) // Normalize to [0, 1]
            .build()
        val processedTensorImage = imageProcessor.process(tensorImage)

        // Create input tensor
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(processedTensorImage.buffer)

        // Run inference
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        // Process the results
        val probabilities = outputFeature0.floatArray // Model predictions

        // Find the index of the class with the highest probability
        val maxProbabilityIndex = probabilities.toList().indexOf(probabilities.maxOrNull())

        // Map the index to the class name
        val classNames = listOf("immature cataract", "mature cataract", "normal eye") // Example class names
        val predictedClassName = classNames[maxProbabilityIndex]

        val confidence = Utils.toPercentString(probabilities[maxProbabilityIndex])

        val textResource = when (predictedClassName) {
            "immature cataract" -> getString(R.string.info_immature)
            "mature cataract" -> getString(R.string.info_mature)
            else -> getString(R.string.infor_normal)
        }

        // Display the result
        viewModel.result.value = Utils.resultCustomStyling(
            this,
            textResource,
            confidence
        )

        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        val timestamp = formatter.format(Date())
        viewModel.saveToHistory(
            timestamp,
            intent.getStringExtra(EXTRA_CAPTURED_IMAGE).toString(),
            viewModel.result.value.toString()
        )

        // Release model resources
        model.close()
    }

    companion object {
        const val EXTRA_CAPTURED_IMAGE = "EXTRA_CAPTURED_IMAGE"
        const val EXTRA_RESULT = "EXTRA_RESULT"
    }
}