package com.example.worder.viewmodel

import androidx.lifecycle.ViewModel
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

class CategoriesViewModel : ViewModel() {

    companion object {
        private const val LANGUAGE_TITLE = "английский"
        private const val PAGE_SIZE = 3
    }

    private val _state =
        MutableStateFlow<CategoriesUiState>(CategoriesUiState.Loading)
    val state: StateFlow<CategoriesUiState> = _state.asStateFlow()

    private var nextPage = 0

    init { loadFirstPage() }

    /** Первая загрузка / повтор после ошибки. */
    fun loadFirstPage() {
        nextPage = 0
        _state.value = CategoriesUiState.Loading
        viewModelScope.launch {
            try {
                val response = CategoryApiClient.categoryApi
                    .getCategories(LANGUAGE_TITLE, page = 0, size = PAGE_SIZE)
                nextPage = 1
                _state.value = CategoriesUiState.Success(
                    categories = response.content,
                    hasMore = !response.last,
                    isLoadingMore = false
                )
            } catch (e: Exception) {
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
                    .getCategories(LANGUAGE_TITLE, page = nextPage, size = PAGE_SIZE)
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