package com.example.pc02valenzuela22101808.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pc02valenzuela22101808.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val user: FirebaseUser? = null,
    val userName: String = "",
    val error: String? = null,
    val isLoggedIn: Boolean = false
)

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        val user = repository.getCurrentUser()
        if (user != null) {
            loadUserName(user.uid)
        }
        _uiState.value = AuthUiState(
            user = user,
            isLoggedIn = user != null
        )
    }

    private fun loadUserName(uid: String) {
        viewModelScope.launch {
            val result = repository.getUserProfile(uid)
            result.fold(
                onSuccess = { appUser ->
                    _uiState.value = _uiState.value.copy(userName = appUser.name)
                },
                onFailure = { }
            )
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Complete todos los campos")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.login(email.trim(), password)
            result.fold(
                onSuccess = { user ->
                    _uiState.value = AuthUiState(user = user, isLoggedIn = true)
                    loadUserName(user.uid)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.localizedMessage ?: "Error al iniciar sesión"
                    )
                }
            )
        }
    }

    fun register(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Complete todos los campos")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.register(name.trim(), email.trim(), password)
            result.fold(
                onSuccess = { user ->
                    _uiState.value = AuthUiState(
                        user = user,
                        userName = name.trim(),
                        isLoggedIn = true
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.localizedMessage ?: "Error al registrarse"
                    )
                }
            )
        }
    }

    fun logout() {
        repository.logout()
        _uiState.value = AuthUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
