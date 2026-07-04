package com.example.pc02valenzuela22101808.presentation.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pc02valenzuela22101808.data.model.Conversion
import com.example.pc02valenzuela22101808.data.model.CurrencyRate
import com.example.pc02valenzuela22101808.data.repository.AuthRepository
import com.example.pc02valenzuela22101808.data.repository.ConversionRepository
import com.example.pc02valenzuela22101808.data.repository.CurrencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ConverterUiState(
    val currencies: List<CurrencyRate> = emptyList(),
    val amount: String = "",
    val fromCurrency: String = "USD",
    val toCurrency: String = "EUR",
    val result: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)

class CurrencyConverterViewModel : ViewModel() {
    private val currencyRepository = CurrencyRepository()
    private val conversionRepository = ConversionRepository()
    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow(ConverterUiState())
    val uiState: StateFlow<ConverterUiState> = _uiState

    init {
        _uiState.value = _uiState.value.copy(
            currencies = currencyRepository.getCurrencies()
        )
    }

    fun updateAmount(amount: String) {
        _uiState.value = _uiState.value.copy(amount = amount, result = null, error = null, saveSuccess = false)
    }

    fun updateFromCurrency(currency: String) {
        _uiState.value = _uiState.value.copy(fromCurrency = currency, result = null, saveSuccess = false)
    }

    fun updateToCurrency(currency: String) {
        _uiState.value = _uiState.value.copy(toCurrency = currency, result = null, saveSuccess = false)
    }

    fun convert() {
        val state = _uiState.value
        val amountText = state.amount.trim()

        if (amountText.isEmpty()) {
            _uiState.value = state.copy(error = "Ingrese un monto")
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null) {
            _uiState.value = state.copy(error = "Ingrese un valor numérico válido")
            return
        }

        if (state.fromCurrency == state.toCurrency) {
            _uiState.value = state.copy(error = "Seleccione monedas diferentes")
            return
        }

        val result = currencyRepository.convert(amount, state.fromCurrency, state.toCurrency)
        val formattedResult = "%.2f %s equivalen a %.2f %s".format(
            amount, state.fromCurrency, result, state.toCurrency
        )

        _uiState.value = state.copy(
            result = formattedResult,
            isLoading = true,
            error = null
        )

        saveConversion(amount, state.fromCurrency, state.toCurrency, result)
    }

    private fun saveConversion(amount: Double, from: String, to: String, result: Double) {
        val user = authRepository.getCurrentUser() ?: run {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "Usuario no autenticado"
            )
            return
        }

        val conversion = Conversion(
            uid = user.uid,
            userEmail = user.email ?: "",
            amount = amount,
            fromCurrency = from,
            toCurrency = to,
            result = result
        )

        viewModelScope.launch {
            val saveResult = conversionRepository.saveConversion(conversion)
            saveResult.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        saveSuccess = true
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error al guardar: ${e.localizedMessage}"
                    )
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
