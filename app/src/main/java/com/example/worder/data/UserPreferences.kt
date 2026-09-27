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

data class UserProfile(
    val gender: Gender?,
    val name: String?
)

class UserPreferences(private val context: Context) {

    // Ключ, по которому лежит имя (аналог ключа в SharedPreferences)
    private val NAME_KEY = stringPreferencesKey("user_name")
    private val GENDER_KEY = stringPreferencesKey("user_gender")

    // Flow — это реактивный поток данных (похож на RxJava Observable или LiveData).
    // Читаем имя: если ключа нет, вернётся null.
    val userProfile: Flow<UserProfile?> = context.dataStore.data
        .map { prefs -> UserProfile(
            Gender.fromString(prefs[GENDER_KEY]),
            prefs[NAME_KEY]
        )}

    // suspend-функция: выполняется в корутине (аналог Future/async в Java)
    suspend fun saveProfile(name: String, gender: Gender) {
        context.dataStore.edit { prefs ->
            prefs[NAME_KEY] = name
            prefs[GENDER_KEY] = gender.name;
        }
    }
}