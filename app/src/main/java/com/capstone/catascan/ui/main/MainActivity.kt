package com.capstone.catascan.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.asLiveData
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.capstone.catascan.R
import com.capstone.catascan.Utils.setFullScreen
import com.capstone.catascan.data.pref.UserPreference
import com.capstone.catascan.data.pref.dataStore
import com.capstone.catascan.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val userPreference: UserPreference by lazy {
        UserPreference.getInstance(dataStore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(binding.root)
        setFullScreen(window)
        setTheme()
        setLanguage()


        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.home, R.id.history, R.id.scan, R.id.profile
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        @Suppress("DEPRECATION")
        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    navController.popBackStack(R.id.home, false)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.history -> {
                    navController.popBackStack(R.id.home, false)
                    navController.navigate(R.id.history)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.scan -> {
                    navController.popBackStack(R.id.home, false)
                    navController.navigate(R.id.scan)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.profile -> {
                    navController.popBackStack(R.id.home, false)
                    navController.navigate(R.id.profile)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }
    }

    private fun setTheme() {
        userPreference.getThemeSetting()
            .asLiveData()
            .observe(this@MainActivity) { isDarkModeActive ->
                // Ensure the emitted value is not null and handle unexpected cases
                val isDarkMode = isDarkModeActive ?: false // Default to light mode if null
                val currentMode = AppCompatDelegate.getDefaultNightMode()

                // Only update the mode if it has changed to avoid redundant calls
                val newMode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                if (currentMode != newMode) {
                    AppCompatDelegate.setDefaultNightMode(newMode)
                }
            }
    }

    private fun setLanguage() {
        userPreference.getLanguageSetting().asLiveData().observe(this@MainActivity) { language ->
            val currentLocale = AppCompatDelegate.getApplicationLocales().toLanguageTags()
            val newLocale = if (language == "Indonesia") "in" else "en"

            if (currentLocale != newLocale) {
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(newLocale)
                )
            }
        }
    }
}
