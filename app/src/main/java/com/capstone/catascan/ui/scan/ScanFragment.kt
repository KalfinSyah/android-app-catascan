package com.capstone.catascan.ui.scan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.capstone.catascan.Utils.createCustomTempFile
import com.capstone.catascan.databinding.FragmentScanBinding
import com.capstone.catascan.ui.scan.preview.PreviewActivity

class ScanFragment : Fragment() {

    // binding
    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!


    // camera
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null


    // for permission
    private val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        val view = binding.root

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        // code here
        binding.switchCameraAngle.setOnClickListener {
            cameraSelector =
                if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA
            startCamera()
        }

        binding.captureBtn.setOnClickListener { takePhoto() }
        binding.galleryBtn.setOnClickListener { startGallery() }

        Glide.with(requireContext())
            .load("https://picsum.photos/201")
            .into(binding.imageView1)
        Glide.with(requireContext())
            .load("https://picsum.photos/202")
            .into(binding.imageView2)
        Glide.with(requireContext())
            .load("https://picsum.photos/203")
            .into(binding.imageView3)
        Glide.with(requireContext())
            .load("https://picsum.photos/204")
            .into(binding.imageView4)

        binding.imageView1.setOnClickListener {
            val intent = Intent(requireContext(), PreviewActivity::class.java)
            intent.putExtra("EXTRA_CAPTURED_IMAGE", "https://picsum.photos/201")
            startActivity(intent)
        }
        binding.imageView2.setOnClickListener {
            val intent = Intent(requireContext(), PreviewActivity::class.java)
            intent.putExtra("EXTRA_CAPTURED_IMAGE", "https://picsum.photos/202")
            startActivity(intent)
        }
        binding.imageView3.setOnClickListener {
            val intent = Intent(requireContext(), PreviewActivity::class.java)
            intent.putExtra("EXTRA_CAPTURED_IMAGE", "https://picsum.photos/203")
            startActivity(intent)
        }
        binding.imageView4.setOnClickListener {
            val intent = Intent(requireContext(), PreviewActivity::class.java)
            intent.putExtra("EXTRA_CAPTURED_IMAGE", "https://picsum.photos/204")
            startActivity(intent)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.surfaceProvider = binding.previewView.surfaceProvider
                }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )

            } catch (exc: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Failed to launch camera",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = createCustomTempFile(requireContext())

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val intent = Intent(requireContext(), PreviewActivity::class.java)
                    intent.putExtra("EXTRA_CAPTURED_IMAGE", output.savedUri.toString())
                    startActivity(intent)
                }

                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(
                        requireContext(),
                        "Failed to capture image.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val intent = Intent(requireContext(), PreviewActivity::class.java)
            intent.putExtra("EXTRA_CAPTURED_IMAGE", uri.toString())
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "No media selected", Toast.LENGTH_SHORT)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}