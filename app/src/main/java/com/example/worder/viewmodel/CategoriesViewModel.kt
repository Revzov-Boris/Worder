package com.example.worder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.worder.data.SettingsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import androidx.lifecycle.viewModelScope
import com.example.worder.data.CategoryDto
import com.example.worder.service.CategoryApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface CategoriesUiState {
    object Loading : CategoriesUiState
    data class Success(
        val categories: List<CategoryDto>,
        val hasMore: Boolean,
        val isLoadingMore: Boolean
    ) : CategoriesUiState
    data class Error(val message: String) : CategoriesUiState
}

class CategoriesViewModel(app: Application) : AndroidViewModel(app) {

    companion object {
        private const val PAGE_SIZE = 3
    }

    private val _state =
        MutableStateFlow<CategoriesUiState>(CategoriesUiState.Loading)
    val state: StateFlow<CategoriesUiState> = _state.asStateFlow()

    private var nextPage = 0
    private var currentLanguage = "Английский"
    private var loadingJob: Job? = null

    private val _language = MutableStateFlow(currentLanguage)
    val language: StateFlow<String> = _language.asStateFlow()

    init {
        // Используем изучаемый язык, сохранённый через DataStore.
        viewModelScope.launch {
            SettingsRepository(app).settings.map { it.language }.distinctUntilChanged().collect { selected ->
                currentLanguage = selected
                _language.value = selected
                loadFirstPage()
            }
        }
    }

    /** Первая загрузка / повтор после ошибки. */
    fun loadFirstPage() {
        nextPage = 0
        _state.value = CategoriesUiState.Loading
        loadingJob?.cancel()
        val requestedLanguage = currentLanguage
        loadingJob = viewModelScope.launch {
            try {
                val response = CategoryApiClient.categoryApi
                    .getCategories(requestedLanguage.lowercase(), page = 0, size = PAGE_SIZE)
                nextPage = 1
                _state.value = CategoriesUiState.Success(
                    categories = response.content,
                    hasMore = !response.last,
                    isLoadingMore = false
                )
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                _state.value = CategoriesUiState.Error(e.message ?: "Ошибка сети")
            }
        }
    }

    /** Подгрузка следующей страницы (кнопка «Больше»). */
    fun loadMore() {
        val current = _state.value
        if (current !is CategoriesUiState.Success) return
        if (!current.hasMore || current.isLoadingMore) return

        _state.value = current.copy(isLoadingMore = true)

        viewModelScope.launch {
            try {
                val response = CategoryApiClient.categoryApi
                    .getCategories(currentLanguage.lowercase(), page = nextPage, size = PAGE_SIZE)
                nextPage++

                val merged = current.categories + response.content
                _state.value = CategoriesUiState.Success(
                    categories = merged,
                    hasMore = !response.last,
                    isLoadingMore = false
                )
            } catch (e: Exception) {
                // Возвращаем прежнее состояние + флаг, чтобы кнопка не пропала
                _state.value = current.copy(isLoadingMore = false)
            }
        }
    }
}