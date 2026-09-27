package com.example.worder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.worder.data.Gender
import com.example.worder.data.UserPreferences
import com.example.worder.data.UserProfile
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// AndroidViewModel даёт доступ к Application (нужен для Context в DataStore)
class UserViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = UserPreferences(app)

    // Превращаем Flow<String?> в StateFlow<String?> для Compose.
    // stateIn запускает поток и держит последнее значение для UI.
    val userProfile = prefs.userProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserProfile(null, null)
    )

    fun saveProfile(name: String, gender: Gender) {
        viewModelScope.launch {   // launch — запуск корутины
            prefs.saveProfile(name.trim(), gender)
        }
    }
}