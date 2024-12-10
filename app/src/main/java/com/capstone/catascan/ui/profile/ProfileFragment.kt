package com.capstone.catascan.ui.profile

import android.Manifest
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.capstone.catascan.R
import com.capstone.catascan.Utils.uriToFile
import com.capstone.catascan.data.pref.UserPreference
import com.capstone.catascan.data.pref.dataStore
import com.capstone.catascan.databinding.FragmentProfileBinding
import com.capstone.catascan.ui.welcome.WelcomeActivity
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.concurrent.TimeUnit

class ProfileFragment : Fragment() {

    // binding
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // preference
    private val userPreference: UserPreference by lazy {
        UserPreference.getInstance(requireContext().dataStore)
    }

    // view model
    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(userPreference)
    }

    // notification
    private lateinit var workManager: WorkManager
    private lateinit var periodicWorkRequest: PeriodicWorkRequest
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
        workManager = WorkManager.getInstance(requireContext())
        setData()
        setAction()
        return view
    }

    override fun onResume() {
        super.onResume()
        setDropDown()
    }

    private fun setAction() {
        // Notification setting toggle
        binding.notification.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            viewModel.saveDailyReminderSetting(isChecked)
        }

        // Dark mode setting toggle
        binding.darkMode.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            viewModel.saveThemeSetting(isChecked)
        }

        // Logout
        binding.logout.setOnClickListener {
            lifecycleScope.launch {
                viewModel.logout(requireContext(), activity?.application as Application)
                val intent = Intent(requireContext(), WelcomeActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }

        // change profile
        binding.changeProfileBtn.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.titlechangepict))
                .setMessage(getString(R.string.changepict))
                .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                    dialog.dismiss()
                    startGallery()
                }
                .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun setData() {

        viewModel.showProgressBar.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it == true) View.VISIBLE else View.GONE
        }

        // set the theme setting
        viewModel.getThemeSetting().observe(viewLifecycleOwner) { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.darkMode.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.darkMode.isChecked = false
            }
        }

        //  set the daily reminder setting
        viewModel.getDailyReminderSetting().observe(viewLifecycleOwner) { isDailyReminderActive ->
            if (isDailyReminderActive) {
                checkNotificationPermission()
                binding.notification.isChecked = true
            } else {
                binding.notification.isChecked = false
            }
        }

        // set the tokenBuffer, and getUserData
        viewModel.getUserSession().observe(viewLifecycleOwner) { user ->
            viewModel.tokenBuffer.value = user.token
            viewModel.getUserData(user.token)
        }


        // upload profile to cloud if the uri is not null
        viewModel.uriBuffer.observe(viewLifecycleOwner) {
            if (it != null) {
                viewModel.uploadProfile(
                    viewModel.tokenBuffer.value.toString(),
                    uriToFile(it, requireContext())
                )
            }
        }



        // after perform getUserData,
        // the getUserResult value will change
        // and load it name, email
        viewModel.getUserResult.observe(viewLifecycleOwner) {
            Glide.with(requireContext())
                .load(it?.profileImageUrl ?: "https://picsum.photos/200")
                .into(binding.profile)
            binding.name.text = getString(R.string.say_hi_to, it.name)
            binding.email.text = it.email
        }


        // only show popup after change the profile
        viewModel.popupMessage.observe(viewLifecycleOwner) {
            if (it.isNotEmpty() && it == "Change Profile Success") {
                showAlertDialogAndDoAction(it) {
                    viewModel.getUserData(viewModel.tokenBuffer.value.toString())
                    viewModel.uriBuffer.value = null
                    viewModel.popupMessage.value = ""
                }
            } else if (it.isNotEmpty() && it != "Change Profile Success"){
                showAlertDialogAndDoAction(it)
                viewModel.uriBuffer.value = null
                viewModel.popupMessage.value = ""
            }
        }
    }

    private fun startPeriodicTask() {
        val data = Data.Builder()
            .build()

        val constraints = Constraints.Builder()
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
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
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

    private fun setDropDown() {
        val language = resources.getStringArray(R.array.languages)
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, language)
        binding.dropDownItem.setAdapter(adapter)
        binding.dropDownItem.setText(language[0], false)

        binding.dropDownItem.setOnItemClickListener { _, _, position, _ ->
            viewModel.saveLanguageSetting(language[position])
        }

        viewModel.getLanguageSetting().observe(viewLifecycleOwner) { languageValue ->
            when (languageValue) {
                "Indonesia", "in" -> {
                    binding.dropDownItem.setText(language[1], false)
                    setLanguageApp("in")
                }
                else -> {
                    binding.dropDownItem.setText(language[0], false)
                    setLanguageApp("en")
                }
            }
        }
    }

    private fun setLanguageApp(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.uriBuffer.value = uri
        } else {
            Toast.makeText(requireContext(), "No media selected", Toast.LENGTH_SHORT)
        }
    }

    private fun showAlertDialogAndDoAction(message: String, action: () -> Unit = {}): Boolean {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setTitle("Message")
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton("Ok") { dialog, _ ->
            dialog.dismiss()
            action()
        }
        alertDialog.create().show()
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}