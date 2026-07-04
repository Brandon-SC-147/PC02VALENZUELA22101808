package com.example.pc02valenzuela22101808.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pc02valenzuela22101808.data.repository.AuthRepository
import com.example.pc02valenzuela22101808.presentation.auth.AuthViewModel
import com.example.pc02valenzuela22101808.presentation.auth.LoginScreen
import com.example.pc02valenzuela22101808.presentation.auth.RegisterScreen
import com.example.pc02valenzuela22101808.presentation.converter.CurrencyConverterScreen
import com.example.pc02valenzuela22101808.presentation.history.HistoryScreen

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val CONVERTER = "converter"
    const val HISTORY = "history"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel(),
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.CONVERTER) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                },
                viewModel = authViewModel
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.CONVERTER) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                viewModel = authViewModel
            )
        }

        composable(Routes.CONVERTER) {
            CurrencyConverterScreen(
                onNavigateToHistory = {
                    navController.navigate(Routes.HISTORY)
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HISTORY) {
            HistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
