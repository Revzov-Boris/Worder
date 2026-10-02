package com.example.worder.ui.compose

import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.worder.data.Gender

// @Composable — это «функция, рисующая UI». Аналог метода onCreate/вёрстки XML,
// но вместо XML всё описывается кодом на Kotlin.
@Composable
fun WelcomeScreen(
    userName: String?,
    userGender: Gender?,
    onEditProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNextClick: () -> Unit
) {

    val helloString = if (!userName.isNullOrBlank() && userGender != null) {
        "Привет, ${userGender.label} $userName"
    } else {
        "Привет"
    }

    val buttonText = if (!userName.isNullOrBlank() && userGender != null) {
        "Изменить данные"
    } else {
        "Войти"
    }

    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = helloString,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onEditProfileClick) {
                Text(buttonText)
            }

            OutlinedButton(onClick = onNextClick) { Text("Далее") }
        }

        IconButton(
            onClick = {
                android.util.Log.d("WELCOME", "settings clicked")
                onSettingsClick()
            },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                "Настройки",
            )
        }
    }
}
