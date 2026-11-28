package com.example.worder.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.worder.data.CategoriesFileInfo
import com.example.worder.viewmodel.SettingsViewModel
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onLogOutClick: () -> Unit,
    onCloseClick: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val settings by viewModel.settings.collectAsState()
    val files by viewModel.files.collectAsState()
    val busy by viewModel.busy.collectAsState()
    val message by viewModel.message.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    var expanded by remember { mutableStateOf(false) }
    var filenameDraft by remember { mutableStateOf(viewModel.fileName.removeSuffix(".txt")) }
    var ageDraft by remember(settings?.age) { mutableStateOf(settings?.age?.toString().orEmpty()) }

    LaunchedEffect(message) {
        message?.let {
            snackbar.showSnackbar(it)
            viewModel.consumeMessage()
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Настройки", style = MaterialTheme.typography.headlineMedium)
            Text("DataStore: язык и тема. SharedPreferences: пол и возраст.",
                style = MaterialTheme.typography.bodySmall)

            if (settings == null) {
                CircularProgressIndicator()
            } else {
                val current = settings!!
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = current.language,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Изучаемый язык (DataStore)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        listOf("Английский", "Испанский", "Немецкий").forEach { value ->
                            DropdownMenuItem(text = { Text(value) }, onClick = {
                                viewModel.setLanguage(value)
                                expanded = false
                            })
                        }
                    }
                }

                OutlinedButton(onClick = { viewModel.setDarkTheme(!current.darkTheme) }) {
                    Icon(
                        imageVector = if (current.darkTheme) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(if (current.darkTheme) "Тёмная тема" else "Светлая тема")
                }

                OutlinedTextField(
                    value = ageDraft,
                    onValueChange = { value ->
                        if (value.length <= 3 && value.all(Char::isDigit)) {
                            ageDraft = value
                            value.toIntOrNull()?.takeIf { it in 1..120 }?.let(viewModel::setAge)
                        }
                    },
                    label = { Text("Возраст (SharedPreferences)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    supportingText = { Text("От 1 до 120 лет") },
                    isError = ageDraft.toIntOrNull()?.let { it in 1..120 } != true,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Пол (SharedPreferences)", style = MaterialTheme.typography.titleSmall)
                listOf("M" to "Мужской", "F" to "Женский", "X" to "Не указан").forEach { (code, label) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = current.gender == code, onClick = { viewModel.setGender(code) })
                        Text(label)
                    }
                }
            }

            HorizontalDivider()
            Text("Файл категорий", style = MaterialTheme.typography.titleLarge)
            Text("Категории со всех страниц сохраняются в общедоступной папке Downloads.",
                style = MaterialTheme.typography.bodySmall)
            OutlinedTextField(
                value = filenameDraft,
                onValueChange = { filenameDraft = it },
                label = { Text("Имя файла без .txt (например, 12_categories)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { viewModel.updateFileName(filenameDraft) },
                enabled = !busy && filenameDraft.isNotBlank()
            ) { Text("Применить имя файла") }

            if (busy) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            when (val publicFile = files?.publicFile) {
                null -> Text("Внешний файл: отсутствует")
                else -> FileDescription("Внешний файл", publicFile)
            }
            when (val backup = files?.backup) {
                null -> Text("Резервная копия: отсутствует")
                else -> FileDescription("Резервная копия доступна", backup)
            }
            Button(
                onClick = viewModel::exportCategories,
                enabled = !busy && files != null && files?.publicFile == null
            ) { Text("Создать файл из категорий") }
            Button(
                onClick = viewModel::deleteFile,
                enabled = !busy && files?.publicFile != null
            ) { Text("Удалить файл (сохранить копию)") }
            Button(
                onClick = viewModel::restoreFile,
                enabled = !busy && files?.backup != null && files?.publicFile == null
            ) { Text("Восстановить из копии") }
            OutlinedButton(onClick = viewModel::refreshFiles, enabled = !busy) {
                Text("Обновить сведения о файлах")
            }
            HorizontalDivider()
            OutlinedButton(onClick = onCloseClick) { Text("Назад") }
            Button(onClick = onLogOutClick) { Text("Выйти из аккаунта") }
        }
    }
}

@Composable
private fun FileDescription(title: String, file: CategoriesFileInfo) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text("Название: ${file.name}")
        Text("Расположение: ${file.location}")
        Text("Размер: ${file.bytes} байт")
        Text("Дата: ${DateFormat.getDateTimeInstance().format(Date(file.created))}")
    }
}
