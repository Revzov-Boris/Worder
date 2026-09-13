package com.example.worder.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Расширение для Context — аналог статического синглтона в Java.
// preferencesDataStore создаёт файл с настройками один раз на всё приложение.
private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    // Ключ, по которому лежит имя (аналог ключа в SharedPreferences)
    private val NAME_KEY = stringPreferencesKey("user_name")

    // Flow — это реактивный поток данных (похож на RxJava Observable или LiveData).
    // Читаем имя: если ключа нет, вернётся null.
    val userName: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[NAME_KEY] }

    // suspend-функция: выполняется в корутине (аналог Future/async в Java)
    suspend fun saveName(name: String) {
        context.dataStore.edit { prefs ->
            prefs[NAME_KEY] = name
        }
    }
}