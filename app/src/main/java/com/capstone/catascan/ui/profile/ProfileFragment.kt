package com.capstone.catascan.ui.profile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.capstone.catascan.R
import com.capstone.catascan.data.pref.SettingPreferences
import com.capstone.catascan.data.pref.dataStore
import com.capstone.catascan.databinding.FragmentProfileBinding
import java.util.concurrent.TimeUnit

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var workManager: WorkManager
    private lateinit var periodicWorkRequest: PeriodicWorkRequest
    private val viewModel: ProfileViewModel by lazy {
        val pref = SettingPreferences.getInstance(requireContext().dataStore)
        ViewModelProvider(this, ProfileViewModelFactory(pref))[ProfileViewModel::class.java]
    }
    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, enable the toggle and call myWorkManager
            binding.notification.isChecked = true
            startPeriodicTask()
        } else {
            // Permission denied, disable the toggle
            binding.notification.isChecked = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        // code here
        workManager = WorkManager.getInstance(requireContext())
        viewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.darkMode.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.darkMode.isChecked = false
            }
        }

        viewModel.getDailyReminderSetting().observe(viewLifecycleOwner) { isDailyReminderActive: Boolean ->
            if (isDailyReminderActive) {
                checkNotificationPermission()
                binding.notification.isChecked = true
            } else {
                binding.notification.isChecked = false
            }
        }

        binding.notification.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            viewModel.saveDailyReminderSetting(isChecked)
        }

        binding.darkMode.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            viewModel.saveThemeSetting(isChecked)
        }

        Glide.with(requireContext())
            .load("https://picsum.photos/200")
            .into(binding.profile)
        binding.name.text = getString(R.string.say_hi_to, "Testing Nama Panjang")
        binding.email.text = getString(R.string.email, "testingnamapanja@gmail.com")

        return view
    }

    private fun startPeriodicTask() {
        val data = Data
            .Builder()
            .build()

        val constraints = Constraints
            .Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        periodicWorkRequest = PeriodicWorkRequest.Builder(MyWorker::class.java, 1, TimeUnit.DAYS)
            .setInputData(data)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "my_periodic_task",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                // Permission already granted
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                    binding.notification.isChecked = true
                    startPeriodicTask()
                }

                // Directly request the permission
                else -> {
                    requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // Below Android 13, permission is not required
            binding.notification.isChecked = true
            startPeriodicTask()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}