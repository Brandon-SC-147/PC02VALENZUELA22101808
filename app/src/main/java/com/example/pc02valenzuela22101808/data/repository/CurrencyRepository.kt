package com.example.pc02valenzuela22101808.data.repository

import com.example.pc02valenzuela22101808.data.model.CurrencyRate

class CurrencyRepository {
    private val currencies = listOf(
        CurrencyRate("USD", "Dólar estadounidense", 1.0),
        CurrencyRate("EUR", "Euro", 0.925),
        CurrencyRate("PEN", "Sol peruano", 3.75),
        CurrencyRate("GBP", "Libra esterlina", 0.79),
        CurrencyRate("JPY", "Yen japonés", 149.5)
    )

    fun getCurrencies(): List<CurrencyRate> = currencies

    fun convert(amount: Double, fromCurrency: String, toCurrency: String): Double {
        val from = currencies.find { it.code == fromCurrency } ?: return 0.0
        val to = currencies.find { it.code == toCurrency } ?: return 0.0
        val amountInUsd = amount / from.rateToUsd
        return amountInUsd * to.rateToUsd
    }
}
