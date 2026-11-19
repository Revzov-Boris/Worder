package com.example.worder.data

data class CategoriesPageDto(
    val content: List<CategoryDto>,
    val totalElements: Int,
    val totalPages: Int,
    val number: Int,      // текущая страница (0-based)
    val size: Int,
    val last: Boolean
)