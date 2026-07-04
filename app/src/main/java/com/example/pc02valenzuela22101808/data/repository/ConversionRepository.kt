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
            val snapshot = conversionsRef
                .whereEqualTo("uid", uid)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val conversions = snapshot.documents.map { doc ->
                Conversion.fromMap(doc.data ?: emptyMap())
            }
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
