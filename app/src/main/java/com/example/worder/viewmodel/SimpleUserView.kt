package com.example.worder.viewmodel

import androidx.lifecycle.ViewModel
import com.example.worder.data.Gender
import com.example.worder.data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SimpleUserView : ViewModel() {
    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile.asStateFlow()

    fun saveProfile(name: String, gender: Gender) {
        _profile.value = UserProfile(gender = gender, name = name)
    }

    fun logOut() {
        _profile.value = null
    }
}