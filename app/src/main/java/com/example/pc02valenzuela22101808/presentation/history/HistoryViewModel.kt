package com.example.pc02valenzuela22101808.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pc02valenzuela22101808.data.model.Conversion
import com.example.pc02valenzuela22101808.data.repository.ConversionRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HistoryUiState(
    val conversions: List<Conversion> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class HistoryViewModel : ViewModel() {
    private val conversionRepository = ConversionRepository()

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState

    init {
        loadHistory()
    }

    fun loadHistory() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            _uiState.value = HistoryUiState(error = "Usuario no autenticado")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = conversionRepository.getConversionsByUser(currentUser.uid)
            result.fold(
                onSuccess = { conversions ->
                    _uiState.value = HistoryUiState(conversions = conversions)
                },
                onFailure = { e ->
                    _uiState.value = HistoryUiState(
                        error = "Error al cargar historial: ${e.localizedMessage}"
                    )
                }
            )
        }
    }
}
