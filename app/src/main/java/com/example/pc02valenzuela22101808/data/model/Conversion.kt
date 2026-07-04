package com.example.pc02valenzuela22101808.data.model

import com.google.firebase.Timestamp

data class Conversion(
    val uid: String = "",
    val userEmail: String = "",
    val amount: Double = 0.0,
    val fromCurrency: String = "",
    val toCurrency: String = "",
    val result: Double = 0.0,
    val createdAt: Timestamp? = null
) {
    fun toMap(): MutableMap<String, Any?> = mutableMapOf(
        "uid" to uid,
        "userEmail" to userEmail,
        "amount" to amount,
        "fromCurrency" to fromCurrency,
        "toCurrency" to toCurrency,
        "result" to result,
        "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): Conversion = Conversion(
            uid = map["uid"] as? String ?: "",
            userEmail = map["userEmail"] as? String ?: "",
            amount = (map["amount"] as? Number)?.toDouble() ?: 0.0,
            fromCurrency = map["fromCurrency"] as? String ?: "",
            toCurrency = map["toCurrency"] as? String ?: "",
            result = (map["result"] as? Number)?.toDouble() ?: 0.0,
            createdAt = map["createdAt"] as? Timestamp
        )
    }
}
