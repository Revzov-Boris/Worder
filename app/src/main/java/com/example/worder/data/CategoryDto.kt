package com.example.worder.data

data class CategoryDto(
    val id: Int,
    val name: String,
    val description: String?,
    val countOfWord: Int
)