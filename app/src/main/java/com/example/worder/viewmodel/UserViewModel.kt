package com.example.worder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.worder.data.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// AndroidViewModel даёт доступ к Application (нужен для Context в DataStore)
class UserViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = UserPreferences(app)

    // Превращаем Flow<String?> в StateFlow<String?> для Compose.
    // stateIn запускает поток и держит последнее значение для UI.
    val userName = prefs.userName.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun saveName(name: String) {
        viewModelScope.launch {   // launch — запуск корутины
            prefs.saveName(name.trim())
        }
    }
}