package com.washwise.mobile.feature.auth.data

import com.washwise.mobile.shared.api.ApiService
import com.washwise.mobile.shared.api.RetrofitClient

/**
 * Data boundary for the auth slice. Wraps the API calls and normalizes backend
 * envelope responses into [Result] so presenters never have to reach for Retrofit.
 */
class AuthRepository(
    private val api: ApiService = RetrofitClient.instance
) {

    suspend fun login(email: String, password: String): Result<AuthResponse> = runCatching {
        val response = api.login(LoginRequest(email, password))
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            error(body?.message ?: "Login failed (HTTP ${response.code()})")
        }
        body.data ?: error("Login response missing data")
    }

    suspend fun register(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Result<AuthResponse> = runCatching {
        val request = RegisterRequest(email, password, confirmPassword, fullName)
        val response = api.register(request)
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            error(body?.message ?: "Registration failed (HTTP ${response.code()})")
        }
        body.data ?: error("Registration response missing data")
    }
}
