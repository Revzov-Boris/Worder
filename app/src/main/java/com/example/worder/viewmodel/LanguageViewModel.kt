package com.example.worder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.worder.data.LanguageDto
import com.example.worder.service.ApiClient
import com.example.worder.service.LanguageApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface LanguagesUiState {
    object Loading : LanguagesUiState
    data class Success(val languages: List<LanguageDto>) : LanguagesUiState
    data class Error(val message: String) : LanguagesUiState
}

class LanguagesViewModel : ViewModel() {
    private val _state = MutableStateFlow<LanguagesUiState>(LanguagesUiState.Loading)
    val state: StateFlow<LanguagesUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        _state.value = LanguagesUiState.Loading
        viewModelScope.launch {
            try {
                val list = ApiClient.languageApi.getLanguages()
                _state.value = LanguagesUiState.Success(list)
            } catch (e: Exception) {
                _state.value = LanguagesUiState.Error(e.message ?: "Ошибка сети")
            }
        }
    }
}