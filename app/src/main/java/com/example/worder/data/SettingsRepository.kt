package com.example.worder.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

// Отдельный Preferences DataStore, не конфликтующий с прежним user_prefs.
private val Context.settingsDataStore by preferencesDataStore(name = "worder_settings")

data class AppSettings(
    val language: String = "Английский",
    val darkTheme: Boolean = false,
    val gender: String = "X",
    val age: Int = 18
)

class SettingsRepository(context: Context) {
    private val appContext = context.applicationContext
    private val shared = appContext.getSharedPreferences("worder_shared_settings", Context.MODE_PRIVATE)

    private val languageKey = stringPreferencesKey("learning_language")
    private val darkThemeKey = booleanPreferencesKey("dark_theme")

    private val sharedValues = MutableStateFlow(
        Pair(shared.getString("gender", "X") ?: "X", shared.getInt("age", 18))
    )

    // DataStore: язык + тема. SharedPreferences: пол + возраст.
    val settings = combine(
        appContext.settingsDataStore.data.map { preferences ->
            Pair(preferences[languageKey] ?: "Английский", preferences[darkThemeKey] ?: false)
        },
        sharedValues
    ) { dataStoreValues, sharedPreferencesValues ->
        AppSettings(
            language = dataStoreValues.first,
            darkTheme = dataStoreValues.second,
            gender = sharedPreferencesValues.first,
            age = sharedPreferencesValues.second
        )
    }

    suspend fun updateLanguage(value: String) {
        appContext.settingsDataStore.edit { it[languageKey] = value }
    }

    suspend fun updateTheme(value: Boolean) {
        appContext.settingsDataStore.edit { it[darkThemeKey] = value }
    }

    fun updateGender(value: String) {
        shared.edit().putString("gender", value).apply()
        sharedValues.value = sharedValues.value.copy(first = value)
    }

    fun updateAge(value: Int) {
        shared.edit().putInt("age", value).apply()
        sharedValues.value = sharedValues.value.copy(second = value)
    }
}
