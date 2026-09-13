package com.example.worder.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// @Composable — это «функция, рисующая UI». Аналог метода onCreate/вёрстки XML,
// но вместо XML всё описывается кодом на Kotlin.
@Composable
fun WelcomeScreen(
    userName: String?,
    onEnterNameClick: () -> Unit
) {
    // Column — вертикальный контейнер (LinearLayout orientation=vertical)
    // Modifier — цепочка настроек: размер, отступы, выравнивание (аналог LayoutParams)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (userName.isNullOrBlank()) {
            Text(
                text = "Привет! Давай познакомимся.",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onEnterNameClick) {
                Text("Ввести имя")
            }
        } else {
            Text(
                text = "Привет, $userName!",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}