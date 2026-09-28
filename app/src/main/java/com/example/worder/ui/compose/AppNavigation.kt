package com.example.worder.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.worder.ui.compose.ProfileInputScreen
import com.example.worder.ui.compose.WelcomeScreen
import com.example.worder.viewmodel.UserViewModel

// Имена маршрутов — как строковые константы
private const val ROUTE_WELCOME = "welcome"
private const val ROUTE_NAME_INPUT = "name_input"
private const val ROUTE_SETTINGS = "settings"
private const val ROUTE_CONTENT = "content"


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: UserViewModel = viewModel()  // получаем ViewModel, привязанную к текущему хосту

    // collectAsState — подписываемся на StateFlow и превращаем в Compose-state
    val profile by viewModel.userProfile.collectAsState()

    NavHost(navController = navController, startDestination = ROUTE_WELCOME) {
        composable(ROUTE_WELCOME) {
            WelcomeScreen(
                userName = profile?.name,
                userGender = profile?.gender,
                onEditProfileClick = { navController.navigate(ROUTE_NAME_INPUT) },
                onSettingsClick = { navController.navigate(ROUTE_SETTINGS) },
                onNextClick = {navController.navigate(ROUTE_CONTENT) }
            )
        }
        composable(ROUTE_NAME_INPUT) {
            ProfileInputScreen(
                initName = profile?.name,
                initGender = profile?.gender,
                onSave = { name, gender ->
                    viewModel.saveProfile(name, gender)
                    navController.popBackStack()  // вернуться назад (на Welcome)
                }
            )
        }
        composable(ROUTE_SETTINGS) {
            SettingsScreen(
                onLogOutClick = {
                    viewModel.logOut()
                    navController.popBackStack()   // возвращаемся на Welcome
                },
                onCloseClick = { navController.popBackStack() }
            )
        }
        composable(ROUTE_CONTENT) {
            ContentScreen(
                onSettingsClick = { navController.navigate(ROUTE_CONTENT) }
            )
        }
    }
}