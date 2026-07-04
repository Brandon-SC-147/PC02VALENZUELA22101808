package com.example.pc02valenzuela22101808.data.repository

import com.example.pc02valenzuela22101808.data.model.AppUser
import com.google.firebase.auth.FirebaseUser

class AuthRepository {
    private val authManager = FirebaseAuthManager()

    fun getCurrentUser(): FirebaseUser? = authManager.getCurrentUser()

    fun isUserLoggedIn(): Boolean = authManager.isUserLoggedIn()

    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return authManager.login(email, password)
    }

    suspend fun register(name: String, email: String, password: String): Result<FirebaseUser> {
        return authManager.register(name, email, password)
    }

    suspend fun getUserProfile(uid: String): Result<AppUser> {
        return authManager.getUserProfile(uid)
    }

    fun logout() {
        authManager.logout()
    }
}
