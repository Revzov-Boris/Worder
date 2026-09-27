package com.example.worder.ui.compose

import androidx.compose.foundation.layout.*
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
    onEditProfileClick: () -> Unit
) {
    // Column — вертикальный контейнер (LinearLayout orientation=vertical)
    // Modifier — цепочка настроек: размер, отступы, выравнивание (аналог LayoutParams)

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
    }
}
