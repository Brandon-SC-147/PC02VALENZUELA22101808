package com.example.pc02valenzuela22101808

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.pc02valenzuela22101808.presentation.auth.AuthViewModel
import com.example.pc02valenzuela22101808.presentation.navigation.AppNavGraph
import com.example.pc02valenzuela22101808.presentation.navigation.Routes
import com.example.pc02valenzuela22101808.ui.theme.PC02VALENZUELA22101808Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PC02VALENZUELA22101808Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App()
                }
            }
        }
    }
}

@Composable
fun App() {
    val authViewModel: AuthViewModel = viewModel()
    val navController = rememberNavController()

    val startDestination = if (authViewModel.uiState.value.isLoggedIn) {
        Routes.CONVERTER
    } else {
        Routes.LOGIN
    }

    AppNavGraph(
        navController = navController,
        authViewModel = authViewModel,
        startDestination = startDestination
    )
}
