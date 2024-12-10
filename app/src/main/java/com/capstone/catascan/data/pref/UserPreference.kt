package com.capstone.catascan.data.pref

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.capstone.catascan.data.UserModel
import com.capstone.catascan.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.util.Locale

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun logout(context: Context, application: Application) {
        try {
            // Clear DataStore
            dataStore.edit { preferences ->
                preferences.clear()
            }

            // Clear cache and files
            context.cacheDir.deleteRecursively()
            context.filesDir.deleteRecursively()

            HistoryRepository(application).deleteAll()

            Log.d("UserPreference", "System databases cleared successfully")
        } catch (e: Exception) {
            Log.d("UserPreference", "Failed to clear all app data.")
            e.printStackTrace()
        }
    }

    fun getSession(): Flow<UserModel> {
        return dataStore.data
            .catch { exception ->
                // Log or handle exception during data retrieval
                exception.printStackTrace()
                emit(emptyPreferences()) // Emit default preferences to avoid crashes
            }
            .map { preferences ->
                UserModel(
                    preferences[EMAIL_KEY] ?: "",
                    preferences[TOKEN_KEY] ?: "",
                    preferences[IS_LOGIN_KEY] ?: false
                )
            }
    }

    suspend fun saveSession(user: UserModel) {
        try {
            dataStore.edit { preferences ->
                preferences[EMAIL_KEY] = user.email
                preferences[TOKEN_KEY] = user.token
                preferences[IS_LOGIN_KEY] = true
            }
        } catch (e: Exception) {
            // Log or handle exception during saveSession
            e.printStackTrace()
        }
    }

    fun getLanguageSetting(): Flow<String> {
        return dataStore.data
            .catch { exception ->
                // Log the error and emit a default value
                exception.printStackTrace()
                emit(emptyPreferences())
            }
            .map { preferences ->
                preferences[LANGUAGE_KEY] ?: Locale.getDefault().language
            }
    }
    suspend fun saveLanguageSetting(language: String) {
        try {
            dataStore.edit { preferences ->
                preferences[LANGUAGE_KEY] = language
            }
        } catch (e: Exception) {
            // Log or handle exception during saveLanguageSetting
            e.printStackTrace()
        }
    }

    fun getThemeSetting(): Flow<Boolean> {
        return dataStore.data
            .catch { exception ->
                // Log the error and emit a default value
                exception.printStackTrace()
                emit(emptyPreferences())
            }
            .map { preferences ->
                preferences[THEME_KEY] ?: false // Default to light mode
            }
    }

    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        try {
            dataStore.edit { preferences ->
                preferences[THEME_KEY] = isDarkModeActive
            }
        } catch (e: Exception) {
            // Log or handle exception during saveThemeSetting
            e.printStackTrace()
        }
    }

    fun getDailyReminderSetting(): Flow<Boolean> {
        return dataStore.data
            .catch { exception ->
                // Log the error and emit a default value
                exception.printStackTrace()
                emit(emptyPreferences())
            }
            .map { preferences ->
                preferences[DAILY_REMINDER_KEY] ?: false // Default to false
            }
    }

    suspend fun saveDailyReminderSetting(isDailyReminderActive: Boolean) {
        try {
            dataStore.edit { preferences ->
                preferences[DAILY_REMINDER_KEY] = isDailyReminderActive
            }
        } catch (e: Exception) {
            // Log or handle exception during saveDailyReminderSetting
            e.printStackTrace()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val THEME_KEY = booleanPreferencesKey("theme_setting")
        private val DAILY_REMINDER_KEY = booleanPreferencesKey("daily_reminder_setting")
        private val LANGUAGE_KEY = stringPreferencesKey("language_setting")

        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}