package com.example.worder.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.worder.data.Gender
import androidx.compose.ui.semantics.Role

@Composable
fun ProfileInputScreen(
    initGender: Gender?,
    initName: String?,
    onSave: (String, Gender) -> Unit
) {
    // remember с ключами: если initialName изменится, пересоздаст состояние.
    // Это позволяет предзаполнить поле текущим именем.
    var name by remember(initName) { mutableStateOf(initName.orEmpty()) }
    var selectedGender by remember(initGender) { mutableStateOf(initGender) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ваши данные",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(24.dp))

        // --- Выбор пола ---
        Text(
            text = "Пол",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup(),   // для доступности (TalkBack)
            verticalAlignment = Alignment.CenterVertically
        ) {
            Gender.entries.forEach { gender ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .selectable(
                            selected = (selectedGender == gender),
                            onClick = { selectedGender = gender },
                            role = Role.RadioButton
                        )
                        .padding(end = 16.dp)
                ) {
                    RadioButton(
                        selected = (selectedGender == gender),
                        onClick = null  // клик обрабатывается на родителе через selectable
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(gender.label)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Ввод имени ---
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Имя") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Кнопка активна, только когда оба поля заполнены
        Button(
            onClick = { selectedGender?.let { onSave(name, it) } },
            enabled = selectedGender != null && name.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сохранить")
        }
    }
}