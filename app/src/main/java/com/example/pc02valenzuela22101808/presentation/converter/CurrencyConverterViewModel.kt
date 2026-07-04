package com.example.pc02valenzuela22101808.presentation.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pc02valenzuela22101808.data.model.Conversion
import com.example.pc02valenzuela22101808.data.model.CurrencyRate
import com.example.pc02valenzuela22101808.data.repository.ConversionRepository
import com.example.pc02valenzuela22101808.data.repository.CurrencyRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ConverterUiState(
    val currencies: List<CurrencyRate> = emptyList(),
    val amount: String = "",
    val fromCurrency: String = "USD",
    val toCurrency: String = "PEN",
    val result: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)

class CurrencyConverterViewModel : ViewModel() {
    private val currencyRepository = CurrencyRepository()
    private val conversionRepository = ConversionRepository()

    private val _uiState = MutableStateFlow(ConverterUiState())
    val uiState: StateFlow<ConverterUiState> = _uiState

    init {
        loadRates()
    }

    fun loadRates() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val rates = currencyRepository.getCurrencies()
            _uiState.value = _uiState.value.copy(
                currencies = rates,
                isLoading = false
            )
        }
    }

    fun updateAmount(amount: String) {
        _uiState.value = _uiState.value.copy(amount = amount, result = null, error = null)
    }

    fun updateFromCurrency(currency: String) {
        _uiState.value = _uiState.value.copy(fromCurrency = currency, result = null)
    }

    fun updateToCurrency(currency: String) {
        _uiState.value = _uiState.value.copy(toCurrency = currency, result = null)
    }

    fun convert() {
        val state = _uiState.value
        val amountValue = state.amount.toDoubleOrNull()

        if (state.amount.isEmpty()) {
            _uiState.value = state.copy(error = "Ingrese un monto")
            return
        }

        if (amountValue == null) {
            _uiState.value = state.copy(error = "Ingrese un valor numérico válido")
            return
        }

        if (state.fromCurrency == state.toCurrency) {
            _uiState.value = state.copy(error = "Seleccione monedas diferentes")
            return
        }

        if (state.currencies.isEmpty()) {
            _uiState.value = state.copy(error = "No se pudieron cargar las tasas")
            return
        }

        val resultValue = currencyRepository.convert(
            amountValue, 
            state.fromCurrency, 
            state.toCurrency, 
            state.currencies
        )

        val formattedResult = "%.2f %s equivalen a %.2f %s".format(
            amountValue, state.fromCurrency, resultValue, state.toCurrency
        )

        _uiState.value = state.copy(result = formattedResult, error = null)

        saveConversion(amountValue, state.fromCurrency, state.toCurrency, resultValue)
    }

    private fun saveConversion(amount: Double, from: String, to: String, result: Double) {
        val user = FirebaseAuth.getInstance().currentUser ?: return

        val conversion = Conversion(
            uid = user.uid,
            userEmail = user.email ?: "",
            amount = amount,
            fromCurrency = from,
            toCurrency = to,
            result = result
        )

        viewModelScope.launch {
            conversionRepository.saveConversion(conversion)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
