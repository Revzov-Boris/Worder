package com.example.yourappname.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.worder.ui.NameInputScreen
import com.example.worder.ui.WelcomeScreen
import com.example.worder.viewmodel.UserViewModel

// Имена маршрутов — как строковые константы
private const val ROUTE_WELCOME = "welcome"
private const val ROUTE_NAME_INPUT = "name_input"

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: UserViewModel = viewModel()  // получаем ViewModel, привязанную к текущему хосту

    // collectAsState — подписываемся на StateFlow и превращаем в Compose-state
    val userName by viewModel.userName.collectAsState()

    NavHost(navController = navController, startDestination = ROUTE_WELCOME) {
        composable(ROUTE_WELCOME) {
            WelcomeScreen(
                userName = userName,
                onEnterNameClick = { navController.navigate(ROUTE_NAME_INPUT) }
            )
        }
        composable(ROUTE_NAME_INPUT) {
            NameInputScreen(
                onSave = { name ->
                    viewModel.saveName(name)
                    navController.popBackStack()  // вернуться назад (на Welcome)
                }
            )
        }
    }
}