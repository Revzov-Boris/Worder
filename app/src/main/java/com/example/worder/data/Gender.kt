package com.example.worder.data

enum class Gender(val label: String) {
    MR("Mr"),
    MRS("Mrs");

    companion object {
        fun fromString(value: String?): Gender? =
            entries.firstOrNull { it.name == value }
    }
}