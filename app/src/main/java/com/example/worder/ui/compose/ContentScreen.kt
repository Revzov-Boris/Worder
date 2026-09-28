package com.example.worder.ui.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.worder.data.LanguageDto
import com.example.worder.viewmodel.LanguagesUiState
import com.example.worder.viewmodel.LanguagesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentScreen(
    onSettingsClick: () -> Unit,
    viewModel: LanguagesViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold() { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val s = state) {
                is LanguagesUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is LanguagesUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Ошибка: ${s.message}")
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.load() }) { Text("Повторить") }
                    }
                }
                is LanguagesUiState.Success -> {
                    if (s.languages.isEmpty()) {
                        Text("Нет данных", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(s.languages, key = { it.id }) { lang ->
                                LanguageCard(lang)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageCard(language: LanguageDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // id — в левом верхнем углу
            Text(
                text = "#${language.id}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.TopStart)
            )

            // Центральный блок: flagCode + title
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = language.flagCode.uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = language.title,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}