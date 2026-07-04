package com.example.pc02valenzuela22101808.data.repository

import com.example.pc02valenzuela22101808.data.model.Conversion
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ConversionRepository {
    private val db = FirebaseFirestore.getInstance()
    private val conversionsRef = db.collection("conversions")
    private val ratesRef = db.collection("rates")

    suspend fun saveConversion(conversion: Conversion): Result<Unit> {
        return try {
            conversionsRef.add(conversion.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getConversionsByUser(uid: String): Result<List<Conversion>> {
        return try {
            // Se filtra por UID para cumplir con las reglas de seguridad de Firestore
            val snapshot = conversionsRef
                .whereEqualTo("uid", uid)
                .get()
                .await()
            
            // Se ordena en memoria para evitar errores de índices inexistentes
            val conversions = snapshot.documents.map { doc ->
                Conversion.fromMap(doc.data ?: emptyMap())
            }.sortedByDescending { it.createdAt?.seconds ?: 0L }

            Result.success(conversions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveRate(rateData: Map<String, Any>): Result<Unit> {
        return try {
            ratesRef.add(rateData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
