package com.example.worder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.worder.data.AppSettings
import com.example.worder.data.CategoriesFileRepository
import com.example.worder.data.CategoriesFilesState
import com.example.worder.data.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    private val settingsRepository = SettingsRepository(app)
    private val fileRepository = CategoriesFileRepository(app)

    val settings: StateFlow<AppSettings?> = settingsRepository.settings.stateIn(
        viewModelScope, SharingStarted.Eagerly, null
    )

    private val _files = MutableStateFlow<CategoriesFilesState?>(null)
    val files = _files.asStateFlow()

    private val _busy = MutableStateFlow(false)
    val busy = _busy.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    val fileName: String get() = fileRepository.fileName

    init { refreshFiles() }

    fun consumeMessage() { _message.value = null }

    fun setGender(value: String) = settingsRepository.updateGender(value)
    fun setAge(value: Int) = settingsRepository.updateAge(value)
    fun setLanguage(value: String) = viewModelScope.launch {
        runCatching { settingsRepository.updateLanguage(value) }.onFailure { _message.value = it.message }
    }
    fun setDarkTheme(value: Boolean) = viewModelScope.launch {
        runCatching { settingsRepository.updateTheme(value) }.onFailure { _message.value = it.message }
    }

    fun updateFileName(value: String) {
        try {
            fileRepository.fileName = value
            refreshFiles()
        } catch (e: Exception) { _message.value = e.message }
    }

    fun refreshFiles() = execute(silent = true) { fileRepository.getState() }
    fun exportCategories() = execute {
        fileRepository.exportCategories(settings.value?.language ?: "Английский")
    }
    fun deleteFile() = execute { fileRepository.deleteWithBackup() }
    fun restoreFile() = execute { fileRepository.restoreBackup() }

    private fun execute(silent: Boolean = false, operation: suspend () -> CategoriesFilesState) {
        if (_busy.value) return
        viewModelScope.launch {
            _busy.value = true
            try {
                _files.value = operation()
                if (!silent) _message.value = "Операция выполнена успешно"
            } catch (e: Exception) {
                _message.value = e.message ?: "Не удалось выполнить операцию"
            } finally {
                _busy.value = false
            }
        }
    }
}
