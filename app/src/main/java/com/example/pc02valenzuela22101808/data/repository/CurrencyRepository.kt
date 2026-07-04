package com.example.pc02valenzuela22101808.data.repository

import com.example.pc02valenzuela22101808.data.model.CurrencyRate
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CurrencyRepository {
    private val db = FirebaseFirestore.getInstance()
    private val ratesRef = db.collection("rates").document("latest")

    private val localFallback = mapOf(
        "USD" to 1.0,
        "EUR" to 0.87352,
        "PEN" to 3.4025,
        "GBP" to 0.735,
        "JPY" to 144.5
    )

    private val currencyCodes = listOf("USD", "EUR", "PEN", "GBP", "JPY")

    suspend fun getCurrencies(): List<CurrencyRate> {
        seedDefaultRatesIfNeeded()
        return try {
            val snapshot = ratesRef.get().await()
            if (snapshot.exists()) {
                val data = snapshot.data ?: emptyMap<String, Any>()
                currencyCodes.map { code ->
                    val rate = (data[code] as? Number)?.toDouble() ?: localFallback[code] ?: 1.0
                    CurrencyRate(code, code, rate)
                }.sortedBy { it.code }
            } else {
                getFallbackRates()
            }
        } catch (e: Exception) {
            getFallbackRates()
        }
    }

    private fun getFallbackRates(): List<CurrencyRate> {
        return localFallback.map { (code, rate) ->
            CurrencyRate(code, code, rate)
        }.sortedBy { it.code }
    }

    private suspend fun seedDefaultRatesIfNeeded() {
        try {
            val snapshot = ratesRef.get().await()
            if (!snapshot.exists()) {
                val data = localFallback.toMutableMap<String, Any>()
                data["updatedAt"] = FieldValue.serverTimestamp()
                ratesRef.set(data).await()
            }
        } catch (e: Exception) {
            // Silently fail
        }
    }

    fun convert(amount: Double, fromCurrency: String, toCurrency: String, rates: List<CurrencyRate>): Double {
        val fromRate = rates.find { it.code == fromCurrency }?.rateToUsd ?: 1.0
        val toRate = rates.find { it.code == toCurrency }?.rateToUsd ?: 1.0
        
        // Formula: resultado = amount / rateFrom * rateTo
        return (amount / fromRate) * toRate
    }
}
