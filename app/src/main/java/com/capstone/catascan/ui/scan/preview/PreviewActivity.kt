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

class PreviewActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityPreviewBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<PreviewViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()

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

        val colorForEyeResult = when (predictedClassName) {
            "immature cataract" -> R.color.yellow
            "mature cataract" -> R.color.red
            else -> R.color.blue
        }

        // Display the result
        viewModel.result.value = Utils.setAndGetTextAndSetColorText(
            this,
            predictedClassName,
            confidence,
            colorForEyeResult
        )

        // Release model resources
        model.close()
    }

    companion object {
        const val EXTRA_CAPTURED_IMAGE = "EXTRA_CAPTURED_IMAGE"
    }
}